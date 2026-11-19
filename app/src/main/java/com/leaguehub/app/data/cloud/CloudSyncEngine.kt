package com.leaguehub.app.data.cloud

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString

/** No remote callback can write Room outside the current authenticated session or erase dirty data. */
data class SyncIssue(val id:String,val title:String,val conflict:Boolean,val error:String?)
data class CloudSyncStatus(val configured:Boolean=false,val bound:Boolean=false,val phase:String="LOCAL",val message:String="Firebase no configurado. La aplicación local sigue disponible.",val pending:Int=0,val lastSync:Long?=null,val issues:List<SyncIssue> = emptyList(),val recoveries:Int=0)
class CloudSyncEngine(
 private val db:LeagueHubDatabase,private val sessions:UserSessionRepository,private val identity:CloudIdentityProvider?,private val remote:LeagueRemote?,private val scope:CoroutineScope
) {
 private val codec=CloudLeagueStore(db)
 private val lock=Mutex()
 private val mutable=MutableStateFlow(CloudSyncStatus(configured=identity!=null))
 val status=mutable.asStateFlow()
 private val wake=Channel<Unit>(Channel.CONFLATED)
 private var started=false
 private fun valid(binding:CloudBindingEntity)=sessions.session.value.userId==binding.userId && identity?.currentUid==binding.uid
 private fun update(phase:String,message:String) { mutable.update { it.copy(phase=phase,message=message) } }
 @OptIn(ExperimentalCoroutinesApi::class,FlowPreview::class)
 fun start() {
  if(started) return;started=true
  if(identity==null || remote==null) return
  scope.launch {
   sessions.restoreCompleted.first { it }
   sessions.session.map { it.userId }.distinctUntilChanged().flatMapLatest { user->if(user==null) flowOf(null) else db.syncDao().observeBinding(user) }.combine(identity.identities) { binding,uid->binding?.takeIf { it.uid==uid } }.distinctUntilChanged().collectLatest { binding ->
    if(binding==null) {
     mutable.value=CloudSyncStatus(configured=true,message="Inicia sesión o conecta tu cuenta para activar Firebase.")
     if(sessions.session.value.userId==null) identity.signOut()
     return@collectLatest
    }
    mutable.value=CloudSyncStatus(true,true,"CONNECTING","Conectando con Firestore…")
    coroutineScope {
     var serverReady=false
     launch {
      db.syncDao().observeAll().collect { rows->
       val own=rows.filter { it.ownerLocalId==binding.userId }
       val leagues=db.competitionDao().allLeagues().first().associateBy { it.id }
       mutable.update { it.copy(pending=own.count { r->r.localVersion>r.syncedVersion || (r.revision==0L && !r.deleted) },issues=rows.filter { it.conflictPayload!=null || it.lastError!=null }.map { r->SyncIssue(r.leagueId,leagues[r.leagueId]?.name ?: "Liga eliminada",r.conflictPayload!=null,r.lastError) },recoveries=db.syncDao().recoveries(binding.userId).size) }
       wake.trySend(Unit)
      }
     }
     launch {
      var failures=0
      while(isActive && valid(binding)) {
       try {
        remote.observe().collect { snapshot ->
         if(snapshot.fromCache) { serverReady=false;update("PENDING","Datos locales disponibles. Esperando conexión con Firestore.") }
         else lock.withLock {
          if(!valid(binding)) return@withLock
          snapshot.leagues.forEach { document ->
           if(!valid(binding)) return@forEach
           try { codec.apply(document,binding) }
           catch(e:Exception) { if(e is CancellationException) throw e
            val row=db.syncDao().league(document.id)
            if(row!=null) db.syncDao().save(row.copy(lastError="No se pudo aplicar una liga remota. Los datos locales siguen intactos."))
            update("ERROR","Una liga remota no es válida; se conservaron tus datos locales.")
           }
          }
          failures=0;serverReady=true
          mutable.update { it.copy(lastSync=System.currentTimeMillis()) }
          wake.trySend(Unit)
         }
        }
       } catch(e:Exception) { if(e is CancellationException) throw e;serverReady=false;update("ERROR",cloudError(e));delay((2000L shl failures.coerceAtMost(4)).coerceAtMost(30_000));failures++ }
      }
     }
     launch {
      while(isActive && valid(binding)) {
       withTimeoutOrNull(10_000) { wake.receive() }
       if(serverReady) lock.withLock { if(valid(binding)) pushDirty(binding) }
      }
     }
     launch {
      sessions.session.map { Triple(it.userName,it.role,it.selectedLeagueId) to it.onboardingCompleted }.distinctUntilChanged().debounce(700).collect {
       if(valid(binding)) {
        val session=sessions.session.value
        try { withTimeout(12_000) { identity.updateProfile(CloudProfile(binding.canonicalId,session.userName.orEmpty(),session.role?.name,session.selectedLeagueId,session.onboardingCompleted)) } }
        catch(e:Exception) { if(e is CancellationException && e !is TimeoutCancellationException) throw e;update("ERROR",cloudError(e)) }
       }
      }
     }
    }
   }
  }
 }
 private suspend fun pushDirty(binding:CloudBindingEntity) {
  val rows=db.syncDao().all().filter { it.ownerLocalId==binding.userId && it.conflictPayload==null && (it.localVersion>it.syncedVersion || (it.revision==0L && !it.deleted)) }
  if(rows.isNotEmpty()) update("SYNCING","Guardando cambios en Firestore…")
  for(row in rows) {
   if(!valid(binding)) return
   try {
    val payload=if(row.deleted) "" else codec.export(row.leagueId)
    if(payload==row.basePayload && row.revision>0) {
     db.withTransaction { db.syncDao().league(row.leagueId)?.let { current->db.syncDao().save(current.copy(syncedVersion=row.localVersion,lastError=null)) } };continue
    }
    when(val result=requireNotNull(remote).push(row.leagueId,binding.uid,binding.canonicalId,row.revision,payload,row.deleted)) {
     is PushResult.Saved->db.withTransaction {
      if(valid(binding)) db.syncDao().league(row.leagueId)?.let { current->db.syncDao().save(current.copy(revision=result.revision,syncedVersion=row.localVersion,basePayload=payload,ownerUid=binding.uid,conflictPayload=null,conflictRevision=0,lastError=null)) }
     }
     is PushResult.Conflict->db.syncDao().league(row.leagueId)?.let { current->db.syncDao().save(current.copy(conflictPayload=CloudLeagueStore.json.encodeToString(result.remote),conflictRevision=result.remote.revision,lastError="Esta liga cambió en otro teléfono. Revisa las versiones antes de continuar.")) }
    }
   } catch(e:Exception) {
    if(e is CancellationException && e !is TimeoutCancellationException) throw e
    db.syncDao().league(row.leagueId)?.let { current->if(current.lastError!=cloudError(e)) db.syncDao().save(current.copy(lastError=cloudError(e))) }
    update("ERROR",cloudError(e));return
   }
  }
  val hasConflict=db.syncDao().all().any { it.conflictPayload!=null }
  update(if(hasConflict) "CONFLICT" else "READY",if(hasConflict) "Hay versiones distintas de una liga. Tus cambios no se han sobrescrito." else "Sincronización al día. Los cambios guardados llegarán a tus otros teléfonos.")
  mutable.update { it.copy(lastSync=System.currentTimeMillis()) }
 }
 fun retry() { wake.trySend(Unit) }
 suspend fun resolve(id:String,keepLocal:Boolean) = lock.withLock {
  val user=requireNotNull(sessions.session.value.userId)
  val binding=requireNotNull(db.syncDao().binding(user)) { "Conecta tu cuenta de Firebase" }
  require(valid(binding)) { "Vuelve a iniciar sesión con Firebase" }
  val row=requireNotNull(db.syncDao().league(id));val document=CloudLeagueStore.json.decodeFromString<RemoteLeague>(requireNotNull(row.conflictPayload))
  if(keepLocal) {
   require(document.ownerUid==binding.uid && row.ownerLocalId==user) { "Solo el dueño puede reemplazar una versión remota" }
   if(!document.deleted) codec.keepRecovery(user,id,document.payload)
   db.syncDao().save(row.copy(revision=document.revision,ownerUid=binding.uid,conflictPayload=null,conflictRevision=0,lastError=null))
  } else {
   if(!row.deleted) codec.keepRecovery(user,id,codec.export(id))
   codec.apply(document,binding,force=true)
  }
  wake.trySend(Unit)
 }
 suspend fun exportRecoveries():String {
  val user=requireNotNull(sessions.session.value.userId)
  val records=db.syncDao().recoveries(user)
  require(records.isNotEmpty()) { "No hay copias de conflictos guardadas" }
  // A single ZIP isn't needed: export an independent backup of the newest recovery.
  val bundle=CloudLeagueStore.json.decodeFromString<CloudLeagueBundle>(records.first().payload)
  val document=BackupDocument("leaguehub-backup",1,java.time.OffsetDateTime.now().toString(),listOf(bundle.league),bundle.clubs,bundle.players,bundle.fixtures,bundle.events,bundle.sheets)
  return kotlinx.serialization.json.Json { prettyPrint=true }.encodeToString(document)
 }
}
