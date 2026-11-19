package com.leaguehub.app.data.cloud

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable data class CloudLeagueBundle(val version:Int=1,val league:BackupLeague,val clubs:List<BackupClub>,val players:List<BackupPlayer>,val fixtures:List<BackupFixture>,val events:List<BackupEvent>,val sheets:List<BackupSheet> = emptyList(),val accountLinks:Map<String,String> = emptyMap())
class CloudLeagueStore(private val db:LeagueHubDatabase) {
 companion object { val json=Json { encodeDefaults=true;ignoreUnknownKeys=false };const val MAX_PAYLOAD=750_000 }
 private val dao get()=db.competitionDao()
 suspend fun export(id:String):String=db.withTransaction {
  val league=requireNotNull(dao.league(id)) { "Liga eliminada" }
  val clubs=dao.allClubs().first().filter { it.leagueId==id };val clubIds=clubs.map { it.id }.toSet()
  val players=dao.allPlayers().first().filter { it.clubId in clubIds }
  val fixtures=dao.allFixtures().first().filter { it.leagueId==id };val fixtureIds=fixtures.map { it.id }.toSet()
  val bindings=db.syncDao().bindings().associate { it.userId to it.uid }
  val bundle=CloudLeagueBundle(league=BackupLeague(league.id,league.name,league.season),clubs=clubs.map { BackupClub(it.id,it.leagueId,it.name) },players=players.map { BackupPlayer(it.id,it.clubId,it.name,it.number,it.position) },fixtures=fixtures.map { BackupFixture(it.id,it.leagueId,it.homeId,it.awayId,it.kickoff,it.venue,it.matchday,it.homeScore,it.awayScore) },events=dao.allEvents().first().filter { it.fixtureId in fixtureIds }.map { BackupEvent(it.id,it.fixtureId,it.playerId,it.kind,it.minute,it.minutesPlayed,it.rating) },sheets=fixtures.mapNotNull { f->db.userDataDao().read(league.ownerId,MatchSheetRepository.key(f.id))?.let { BackupSheet(f.id,MatchSheetRepository.decode(it).copy(photos=emptyList())) } },accountLinks=players.mapNotNull { p->p.accountId?.let { account->bindings[account]?.let { p.id to it } } }.toMap())
  json.encodeToString(bundle).also { require(it.toByteArray().size<=MAX_PAYLOAD) { "La liga supera 750 KB. Conserva un respaldo local antes de dividirla en temporadas." } }
 }
 fun decode(remote:RemoteLeague):CloudLeagueBundle {
  require(remote.revision>0 && remote.payload.toByteArray().size<=MAX_PAYLOAD) { "Revisión remota inválida" }
  val bundle=json.decodeFromString<CloudLeagueBundle>(remote.payload)
  require(bundle.version==1 && bundle.league.id==remote.id) { "Formato de liga remota no compatible" }
  require(bundle.accountLinks.keys.all { key->bundle.players.any { it.id==key } } && bundle.accountLinks.values.all { it.length in 1..128 }) { "Vínculos de jugador inválidos" }
  BackupRepository(db).validate(BackupDocument("leaguehub-backup",1,"2000-01-01T00:00:00Z",listOf(bundle.league),bundle.clubs,bundle.players,bundle.fixtures,bundle.events,bundle.sheets))
  return bundle
 }
 private suspend fun removeLeague(id:String) {
  val fixtures=dao.allFixtures().first().filter { it.leagueId==id };val clubs=dao.allClubs().first().filter { it.leagueId==id };val clubIds=clubs.map { it.id }.toSet()
  fixtures.forEach { fixture->dao.delete(fixture);db.userDataDao().allMatchSheets().first().filter { it.dataKey==MatchSheetRepository.key(fixture.id) }.forEach { db.userDataDao().delete(it.userId,it.dataKey) } }
  dao.allPlayers().first().filter { it.clubId in clubIds }.forEach { dao.delete(it) }
  clubs.forEach { dao.delete(it) };dao.league(id)?.let { dao.delete(it) }
 }
 /** Caller holds the engine mutex; version check and trigger suppression are transactional. */
 suspend fun apply(remote:RemoteLeague,binding:CloudBindingEntity,force:Boolean=false):Boolean=db.withTransaction {
  val old=db.syncDao().league(remote.id)
  if(!force && old!=null && remote.revision<=old.revision) return@withTransaction true
  if(!force && old!=null && old.localVersion>old.syncedVersion) {
   db.syncDao().save(old.copy(conflictPayload=json.encodeToString(remote),conflictRevision=remote.revision,lastError="Otro teléfono cambió esta liga. Elige qué versión conservar."));return@withTransaction false
  }
  val bundle=if(remote.deleted) null else decode(remote)
  val oldLeague=dao.league(remote.id)
  val localOwner=if(remote.ownerUid==binding.uid) binding.userId else "firebase-owner:${remote.ownerUid}"
  require(oldLeague==null || old==null || old.ownerUid==null || old.ownerUid==remote.ownerUid) { "La propiedad remota de la liga cambió" }
  val oldLinks=dao.allPlayers().first().associate { it.id to it.accountId }
  val photos=oldLeague?.let { league->dao.allFixtures().first().filter { it.leagueId==remote.id }.associate { it.id to MatchSheetRepository.decode(db.userDataDao().read(league.ownerId,MatchSheetRepository.key(it.id))).photos } }.orEmpty()
  db.syncDao().suppress(true)
  try {
   removeLeague(remote.id)
   bundle?.let { b->
    // Do not allow an incoming document to overwrite a different league's entity IDs.
    val clubs=dao.allClubs().first().map { it.id }.toSet();val players=dao.allPlayers().first().map { it.id }.toSet();val fixtures=dao.allFixtures().first().map { it.id }.toSet();val events=dao.allEvents().first().map { it.id }.toSet()
    require(b.clubs.none { it.id in clubs } && b.players.none { it.id in players } && b.fixtures.none { it.id in fixtures } && b.events.none { it.id in events }) { "Hay IDs remotos usados por otra liga. No se modificaron tus datos." }
    dao.save(LeagueEntity(b.league.id,localOwner,b.league.name,b.league.season))
    b.clubs.forEach { dao.save(ClubEntity(it.id,it.leagueId,it.name)) }
    val mappings=db.syncDao().bindings().associate { it.uid to it.userId }
    val linked=dao.allPlayers().first().mapNotNull { it.accountId }.toMutableSet()
    b.players.forEach { p->val account=(b.accountLinks[p.id]?.let { mappings[it] ?: it } ?: oldLinks[p.id])?.takeIf { linked.add(it) };dao.save(PlayerEntity(p.id,p.clubId,p.name,p.number,p.position,account)) }
    b.fixtures.forEach { dao.save(FixtureEntity(it.id,it.leagueId,it.homeId,it.awayId,it.kickoff,it.venue,it.matchday,it.homeScore,it.awayScore)) }
    b.events.forEach { dao.save(MatchEventEntity(it.id,it.fixtureId,it.playerId,it.kind,it.minute,it.minutesPlayed,it.rating)) }
    b.sheets.forEach { entry->val sheet=entry.sheet.copy(photos=photos[entry.fixtureId].orEmpty());db.userDataDao().upsert(UserDataEntity(localOwner,MatchSheetRepository.key(entry.fixtureId),MatchSheetRepository.json.encodeToString(sheet))) }
    // Gallery-only local sheets also survive a remote acta without photos.
    photos.filterKeys { id->b.fixtures.any { it.id==id } && b.sheets.none { it.fixtureId==id } }.forEach { (id,uris)->if(uris.isNotEmpty()) db.userDataDao().upsert(UserDataEntity(localOwner,MatchSheetRepository.key(id),MatchSheetRepository.json.encodeToString(MatchSheet(photos=uris)))) }
   }
   val version=old?.localVersion ?: 0
   db.syncDao().save(SyncLeagueEntity(remote.id,localOwner,remote.revision,version,version,remote.deleted,remote.payload,remote.ownerUid))
  } finally { db.syncDao().suppress(false) }
  true
 }
 suspend fun keepRecovery(userId:String,id:String,payload:String) {
  db.syncDao().save(SyncRecoveryEntity(java.util.UUID.randomUUID().toString(),id,userId,payload,System.currentTimeMillis()))
 }
}
