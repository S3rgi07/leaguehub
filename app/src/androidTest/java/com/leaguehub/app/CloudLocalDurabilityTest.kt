package com.leaguehub.app
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.JsonParser
import com.leaguehub.app.data.cloud.*
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class CloudLocalDurabilityTest {
 @Test fun versionFourMigratesSportsDataAndTracksEditsInTheSameTransaction()=runBlocking {
  val context=ApplicationProvider.getApplicationContext<Context>();val name="v4-cloud-${UUID.randomUUID()}.db"
  val schema=InstrumentationRegistry.getInstrumentation().context.assets.open("com.leaguehub.app.data.local.LeagueHubDatabase/4.json").bufferedReader().use { JsonParser.parseString(it.readText()).asJsonObject.getAsJsonObject("database") }
  context.openOrCreateDatabase(name,Context.MODE_PRIVATE,null).use { old ->
   schema.getAsJsonArray("entities").forEach { element->val entity=element.asJsonObject;val table=entity.get("tableName").asString;old.execSQL(entity.get("createSql").asString.replace("\${TABLE_NAME}",table));entity.getAsJsonArray("indices")?.forEach { old.execSQL(it.asJsonObject.get("createSql").asString.replace("\${TABLE_NAME}",table)) } }
   old.execSQL("INSERT INTO leagues VALUES('legacy-l','local-owner','Liga anterior','2026')")
   old.execSQL("INSERT INTO clubs VALUES('legacy-c','legacy-l','Equipo anterior')")
   old.version=4
  }
  val db=Room.databaseBuilder(context,LeagueHubDatabase::class.java,name).addMigrations(LeagueHubDatabase.MIGRATION_4_5).addCallback(LeagueHubDatabase.SYNC_CALLBACK).build()
  try {
   assertEquals("Liga anterior",db.competitionDao().league("legacy-l")?.name)
   val first=db.syncDao().league("legacy-l")!!;assertTrue(first.localVersion>first.syncedVersion)
   val repo=CompetitionRepository(db);repo.saveClub("local-owner",ClubEntity("legacy-c","legacy-l","Cambio durable"))
   val second=db.syncDao().league("legacy-l")!!;assertTrue(second.localVersion>first.localVersion)
   // FK failures roll back both the data and outbox instead of queueing a fictitious change.
   val before=db.syncDao().league("legacy-l")!!
   assertTrue(runCatching { repo.savePlayer("local-owner",PlayerEntity("bad","missing","Inválido",10,"Defensa")) }.isFailure)
   assertEquals(before.localVersion,db.syncDao().league("legacy-l")!!.localVersion)
   repo.delete("local-owner","club","legacy-c");repo.delete("local-owner","league","legacy-l")
   assertTrue(db.syncDao().league("legacy-l")!!.deleted)
  } finally { db.close();context.deleteDatabase(name) }
 }
 @Test fun cloudApplyIsAtomicAndKeepsPrivatePhotosLocal()=runBlocking {
  val context=ApplicationProvider.getApplicationContext<Context>()
  val db=Room.inMemoryDatabaseBuilder(context,LeagueHubDatabase::class.java).addCallback(LeagueHubDatabase.SYNC_CALLBACK).build()
  val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
  try {
   val sessions=UserSessionRepository(db.userSessionDao(),scope,db.userAccountDao());sessions.restoreCompleted.first { it }
   AuthRepository(db,sessions).login(DemoAccount.EMAIL,DemoAccount.PASSWORD)
   val owner=DemoAccount.USER_ID;val binding=CloudBindingEntity(owner,"cloud-demo","cloud-demo");db.syncDao().save(binding)
   val codec=CloudLeagueStore(db);val initial=codec.export(DemoAccount.LEAGUE_ID)
   val fixture=db.competitionDao().allFixtures().first().first()
   MatchSheetRepository(db).update(owner,fixture.id) { it.copy(photos=listOf("content://local-only/photo")) }
   assertFalse(codec.export(DemoAccount.LEAGUE_ID).contains("content://"))
   assertFalse(initial.contains("passwordHash"));assertFalse(initial.contains(DemoAccount.EMAIL))
   val row=db.syncDao().league(DemoAccount.LEAGUE_ID)!!;db.syncDao().save(row.copy(syncedVersion=row.localVersion))
   val remote=RemoteLeague(DemoAccount.LEAGUE_ID,"cloud-demo","cloud-demo",1,initial)
   assertTrue(codec.apply(remote,binding))
   assertEquals(listOf("content://local-only/photo"),MatchSheetRepository.decode(db.userDataDao().read(owner,MatchSheetRepository.key(fixture.id))).photos)
   val after=db.syncDao().league(DemoAccount.LEAGUE_ID)!!;assertEquals(after.localVersion,after.syncedVersion)
   val data=CloudLeagueStore.json.decodeFromString<CloudLeagueBundle>(initial)
   val invalid=data.copy(clubs=data.clubs.map { it.copy(leagueId="not-the-league") })
   val oldCount=db.competitionDao().allPlayers().first().size
   assertTrue(runCatching { codec.apply(remote.copy(revision=2,payload=CloudLeagueStore.json.encodeToString(CloudLeagueBundle.serializer(),invalid)),binding) }.isFailure)
   assertEquals(oldCount,db.competitionDao().allPlayers().first().size)
   assertEquals(1,db.syncDao().league(DemoAccount.LEAGUE_ID)!!.revision)
  } finally { scope.cancel();db.close() }
 }
}
