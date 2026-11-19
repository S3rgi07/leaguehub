package com.leaguehub.app.data.cloud

import com.google.firebase.auth.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import java.util.UUID

class FirebaseGateway(val auth:FirebaseAuth,val firestore:FirebaseFirestore):CloudIdentityProvider,LeagueRemote {
 override val currentUid get()=auth.currentUser?.uid
 override val identities:Flow<String?> = callbackFlow {
  val listener=FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
  auth.addAuthStateListener(listener);awaitClose { auth.removeAuthStateListener(listener) }
 }
 override suspend fun connect(email:String,password:String,localId:String?,localName:String?,localPasswordValid:Boolean,register:Boolean):CloudIdentity=withTimeout(20_000) {
  val result=if(register) auth.createUserWithEmailAndPassword(email,password).awaitCloud() else try {
   auth.signInWithEmailAndPassword(email,password).awaitCloud()
  } catch(e:FirebaseAuthException) {
   if(localPasswordValid && (e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException)) auth.createUserWithEmailAndPassword(email,password).awaitCloud() else throw e
  }
  val user=requireNotNull(result.user)
  val reference=firestore.collection("users").document(user.uid)
  firestore.runTransaction { transaction ->
   val old=transaction.get(reference)
   if(!old.exists()) {
    val profile=mapOf("canonicalId" to user.uid,"name" to (localName ?: user.displayName ?: email.substringBefore('@')),"role" to null,"selectedLeagueId" to null,"onboardingCompleted" to false,"updatedAt" to FieldValue.serverTimestamp())
    transaction.set(reference,profile)
    CloudIdentity(user.uid,profile.getValue("canonicalId") as String,profile.getValue("name") as String,null,null,false)
   } else CloudIdentity(user.uid,requireNotNull(old.getString("canonicalId")),old.getString("name") ?: email.substringBefore('@'),old.getString("role"),old.getString("selectedLeagueId"),old.getBoolean("onboardingCompleted") ?: false)
  }.awaitCloud()
 }
 override suspend fun sendPasswordReset(email:String) { withTimeout(20_000) { auth.sendPasswordResetEmail(email).awaitCloud() } }
 override suspend fun changePassword(uid:String,email:String,current:String,replacement:String)=withTimeout(20_000) {
  val user=requireNotNull(auth.currentUser) { "Inicia sesión con Firebase antes de cambiar la contraseña" }
  require(user.uid==uid) { "La sesión de Firebase no corresponde a esta cuenta" }
  user.reauthenticate(EmailAuthProvider.getCredential(email,current)).awaitCloud()
  user.updatePassword(replacement).awaitCloud()
  Unit
 }
 override suspend fun updateProfile(profile:CloudProfile) {
  val uid=requireNotNull(currentUid)
  firestore.collection("users").document(uid).set(mapOf("canonicalId" to profile.canonicalId,"name" to profile.name,"role" to profile.role,"selectedLeagueId" to profile.selectedLeagueId,"onboardingCompleted" to profile.onboardingCompleted,"updatedAt" to FieldValue.serverTimestamp())).awaitCloud()
 }
 override fun signOut()=auth.signOut()
 private fun decode(document:DocumentSnapshot)=RemoteLeague(document.id,requireNotNull(document.getString("ownerUid")),requireNotNull(document.getString("ownerLocalId")),requireNotNull(document.getLong("revision")),document.getString("payload") ?: "",document.getBoolean("deleted") ?: false)
 override fun observe():Flow<RemoteSnapshot> = callbackFlow {
  val registration=firestore.collection("leagues").addSnapshotListener(MetadataChanges.INCLUDE) { snapshot,error ->
   if(error!=null) close(error)
   else if(snapshot!=null && !snapshot.metadata.hasPendingWrites()) {
    runCatching { snapshot.documents.map(::decode) }.onSuccess { trySend(RemoteSnapshot(it,snapshot.metadata.isFromCache)) }.onFailure { close(it) }
   }
  }
  awaitClose { registration.remove() }
 }
 override suspend fun push(id:String,ownerUid:String,ownerLocalId:String,expectedRevision:Long,payload:String,deleted:Boolean):PushResult=withTimeout(25_000) {
  require(payload.toByteArray(Charsets.UTF_8).size<=750_000) { "La liga supera el tamaño admitido por la sincronización (750 KB). Exporta un respaldo; los datos locales se conservan." }
  val reference=firestore.collection("leagues").document(id)
  firestore.runTransaction { transaction ->
   val old=transaction.get(reference)
   if(old.exists() && old.getLong("revision")!=expectedRevision) PushResult.Conflict(decode(old))
   else {
    require(!old.exists() || old.getString("ownerUid")==ownerUid) { "La liga pertenece a otra cuenta de Firebase" }
    require(old.exists() || expectedRevision==0L) { "Falta una revisión remota. No se sobrescribirá la liga." }
    val revision=expectedRevision+1
    transaction.set(reference,mapOf("ownerUid" to ownerUid,"ownerLocalId" to ownerLocalId,"revision" to revision,"payload" to payload,"deleted" to deleted,"updatedAt" to FieldValue.serverTimestamp()))
    PushResult.Saved(revision)
   }
  }.awaitCloud()
 }
}
