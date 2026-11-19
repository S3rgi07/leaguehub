package com.leaguehub.app.data.repository

import androidx.room.withTransaction
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.session.UserSession
import com.leaguehub.app.data.cloud.*
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val database:LeagueHubDatabase,private val sessionRepository:UserSessionRepository,private val cloud:CloudIdentityProvider?=null) {
 private val notice=MutableStateFlow<String?>(null)
 val cloudNotice=notice.asStateFlow()
 private fun normalized(email:String)=email.trim().lowercase(Locale.ROOT)
 private fun fallback(error:Exception):Boolean = error is com.google.firebase.FirebaseNetworkException || error is com.google.firebase.firestore.FirebaseFirestoreException || error is TimeoutCancellationException || (error is com.google.firebase.auth.FirebaseAuthException && (error.errorCode.contains("OPERATION_NOT_ALLOWED") || error.errorCode.contains("INTERNAL_ERROR") || error.errorCode.contains("CONFIGURATION")))
 private suspend fun identity(email:String,password:String,local:UserAccountEntity?,name:String?=null,register:Boolean=false):CloudIdentity? {
  val provider=cloud ?: return null
  return try { provider.connect(email,password,local?.userId,name ?: local?.name,local!=null && PasswordHasher.matches(password,local.passwordSalt,local.passwordHash),register).also { notice.value=null } }
  catch(e:Exception) {
   if(e is CancellationException && e !is TimeoutCancellationException) throw e
   notice.value=cloudError(e)
   if(fallback(e) && (register || (local!=null && PasswordHasher.matches(password,local.passwordSalt,local.passwordHash)))) null else throw IllegalArgumentException(cloudError(e),e)
  }
 }
 private suspend fun bind(account:UserAccountEntity,identity:CloudIdentity) {
  val previous=database.syncDao().binding(account.userId)
  require(previous==null || previous.uid==identity.uid) { "La cuenta de Firebase no coincide con el vínculo local" }
  database.syncDao().save(CloudBindingEntity(account.userId,identity.uid,identity.canonicalId))
  if(previous==null) database.syncDao().mappingChanged(account.userId)
 }
 suspend fun register(name:String,email:String,password:String)=withContext(Dispatchers.IO) {
  val mail=normalized(email)
  require(database.userAccountDao().findByEmail(mail)==null) { "Ya existe una cuenta con este correo. Inicia sesión para vincularla." }
  val remote=identity(mail,password,null,name.trim(),register=true)
  val salt=PasswordHasher.newSalt()
  val account=UserAccountEntity(remote?.canonicalId ?: UUID.randomUUID().toString(),name.trim(),mail,PasswordHasher.hash(password,salt),salt)
  sessionRepository.replacePersistedSession(account.toSession()) { database.withTransaction {
   database.userAccountDao().insert(account)
   remote?.let { bind(account,it) }
   database.userSessionDao().upsert(UserSessionEntity.fromDomain(account.toSession()))
  } }
 }
 suspend fun login(email:String,password:String):UserSession=withContext(Dispatchers.IO) {
  val mail=normalized(email)
  val seedDemo=mail==DemoAccount.EMAIL && password==DemoAccount.PASSWORD && database.userAccountDao().findByEmail(mail)==null
  if(mail==DemoAccount.EMAIL && password==DemoAccount.PASSWORD) DemoAccount.ensureCreated(database)
  sessionRepository.authenticate {
   val local=database.userAccountDao().findByEmail(mail)
   val remote=identity(mail,password,local)
   require(remote!=null || (local!=null && PasswordHasher.matches(password,local.passwordSalt,local.passwordHash))) { "Correo o contraseña incorrectos" }
   val salt=PasswordHasher.newSalt()
   val account=if(local==null) {
    val r=requireNotNull(remote)
    UserAccountEntity(r.canonicalId,r.name,mail,PasswordHasher.hash(password,salt),salt,r.role?.takeIf { it in listOf("PLAYER","ORGANIZER") },r.selectedLeagueId,r.onboardingCompleted).also { database.userAccountDao().insert(it) }
   } else {
    if(remote!=null) database.userAccountDao().updatePassword(local.userId,PasswordHasher.hash(password,salt),salt)
    local
   }
   if(account.userId==DemoAccount.USER_ID) DemoAccount.ensureSheets(database)
   remote?.let { r->database.withTransaction {
    bind(account,r)
    // A just-generated demo is a fallback, not a competing edit of the other phone's demo.
    if(seedDemo) database.syncDao().league(DemoAccount.LEAGUE_ID)?.let { row->database.syncDao().save(row.copy(syncedVersion=row.localVersion)) }
   } }
   account.toSession()
  }
  sessionRepository.session.value
 }
 suspend fun connectCurrent(password:String)=withContext(Dispatchers.IO) {
  require(cloud!=null) { "Falta google-services.json. Compila de nuevo después de añadirlo a app/." }
  val session=sessionRepository.session.value
  val local=requireNotNull(session.userId?.let { database.userAccountDao().findById(it) }) { "Inicia sesión con una cuenta local válida" }
  val remote=try { cloud.connect(local.email,password,local.userId,local.name,PasswordHasher.matches(password,local.passwordSalt,local.passwordHash)) }
   catch(e:Exception) { if(e is CancellationException) throw e;throw IllegalArgumentException(cloudError(e),e) }
  require(sessionRepository.session.value.userId==local.userId) { "La sesión cambió; vuelve a iniciar sesión" }
  database.withTransaction {
   bind(local,remote)
   val salt=PasswordHasher.newSalt();database.userAccountDao().updatePassword(local.userId,PasswordHasher.hash(password,salt),salt)
  }
  notice.value=null
 }
 suspend fun updateName(name:String)=withContext(Dispatchers.IO) {
  require(name.trim().length>=3) { "Ingresa tu nombre completo" }
  sessionRepository.authenticate {
   val session=sessionRepository.session.value;val id=requireNotNull(session.userId) { "Inicia sesión de nuevo" }
   require(database.userAccountDao().findById(id)!=null) { "Esta sesión antigua requiere crear una cuenta" }
   database.userAccountDao().updateName(id,name.trim());session.copy(userName=name.trim())
  }
 }
 suspend fun changePassword(current:String,replacement:String)=withContext(Dispatchers.IO) {
  require(replacement.length>=8 && replacement.any(Char::isUpperCase) && replacement.any(Char::isDigit)) { "Usa al menos 8 caracteres, una mayúscula y un número" }
  sessionRepository.authenticate {
   val session=sessionRepository.session.value
   val account=requireNotNull(session.userId?.let { database.userAccountDao().findById(it) }) { "Esta sesión antigua requiere crear una cuenta" }
   val binding=database.syncDao().binding(account.userId)
   if(cloud!=null && binding!=null) try { cloud.changePassword(binding.uid,account.email,current,replacement) }
    catch(e:Exception) { if(e is CancellationException) throw e;throw IllegalArgumentException(cloudError(e),e) }
   else require(PasswordHasher.matches(current,account.passwordSalt,account.passwordHash)) { "La contraseña actual es incorrecta" }
   val salt=PasswordHasher.newSalt();database.userAccountDao().updatePassword(account.userId,PasswordHasher.hash(replacement,salt),salt);session
  }
 }
 private fun UserAccountEntity.toSession()=UserSession(userId=userId,userName=name,email=email,role=role?.let(SessionRole::valueOf),selectedLeagueId=selectedLeagueId,isAuthenticated=true,onboardingCompleted=onboardingCompleted)
}
