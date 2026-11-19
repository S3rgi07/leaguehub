package com.leaguehub.app.data.cloud

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

suspend fun <T> Task<T>.awaitCloud():T=suspendCancellableCoroutine { continuation ->
 addOnCompleteListener { task -> if(continuation.isActive) {
  if(task.isSuccessful) continuation.resume(task.result) else continuation.resumeWithException(task.exception ?: IllegalStateException("Operación de Firebase cancelada"))
 } }
}
data class CloudIdentity(val uid:String,val canonicalId:String,val name:String,val role:String?,val selectedLeagueId:String?,val onboardingCompleted:Boolean)
data class CloudProfile(val canonicalId:String,val name:String,val role:String?,val selectedLeagueId:String?,val onboardingCompleted:Boolean)
interface CloudIdentityProvider {
 val currentUid:String?
 val identities:Flow<String?>
 suspend fun connect(email:String,password:String,localId:String?,localName:String?,localPasswordValid:Boolean,register:Boolean=false):CloudIdentity
 suspend fun sendPasswordReset(email:String)
 suspend fun changePassword(uid:String,email:String,current:String,replacement:String)
 suspend fun updateProfile(profile:CloudProfile)
 fun signOut()
}
@Serializable data class RemoteLeague(val id:String,val ownerUid:String,val ownerLocalId:String,val revision:Long,val payload:String,val deleted:Boolean=false)
sealed interface PushResult { data class Saved(val revision:Long):PushResult;data class Conflict(val remote:RemoteLeague):PushResult }
data class RemoteSnapshot(val leagues:List<RemoteLeague>,val fromCache:Boolean=false)
interface LeagueRemote {
 fun observe():Flow<RemoteSnapshot>
 suspend fun push(id:String,ownerUid:String,ownerLocalId:String,expectedRevision:Long,payload:String,deleted:Boolean):PushResult
}
fun cloudError(error:Throwable):String = when(error) {
 is com.google.firebase.FirebaseNetworkException -> "Sin conexión con Firebase. Tus cambios siguen guardados en el teléfono."
 is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException, is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Correo o contraseña de Firebase incorrectos."
 is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "La cuenta ya existe en Firebase. Inicia sesión con su contraseña."
 is com.google.firebase.FirebaseTooManyRequestsException -> "Firebase limitó temporalmente los intentos. Vuelve a probar más tarde."
 is com.google.firebase.firestore.FirebaseFirestoreException -> when(error.code) {
  com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED->"Firestore rechazó el acceso. Revisa las reglas publicadas y vuelve a iniciar sesión."
  com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE->"Firestore no está disponible. Se reintentará sin borrar tus cambios locales."
  com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAUTHENTICATED->"Vuelve a iniciar sesión para sincronizar con Firebase."
  else->"No se pudo sincronizar con Firestore. Tus datos locales están conservados."
 }
 is kotlinx.coroutines.TimeoutCancellationException -> "Firebase no respondió a tiempo. Tus datos locales están conservados."
 else -> if(error is IllegalArgumentException) error.message ?: "Revisa la configuración de Firebase." else "Firebase no está listo. Revisa Authentication, Firestore y las reglas del proyecto."
}
