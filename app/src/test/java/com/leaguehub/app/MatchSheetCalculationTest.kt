package com.leaguehub.app
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import org.junit.Assert.*
import org.junit.Test
class MatchSheetCalculationTest {
 private val fixture=FixtureEntity("f","l","a","b","2026-11-19T18:00","Central",1)
 private val players=(1..6).map { PlayerEntity("p$it","a","Jugador $it",it,"Defensa") }
 private val initial=MatchSheet(teams=listOf(TeamSheet("a","2-2",listOf("p1","p2","p3","p4","p5"))))
 @Test fun substitutesAndReentriesAccumulateOnlyTheirIntervals() {
  val sheet=initial.copy(substitutions=listOf(Substitution("a","p1","p6",65),Substitution("a","p6","p1",80)))
  MatchSheetRepository.validate(sheet,fixture,players)
  assertEquals("p1",MatchSheetRepository.playersOnCourt(sheet,"a",64).first())
  assertEquals("p6",MatchSheetRepository.playersOnCourt(sheet,"a",65).first())
  assertEquals("p1",MatchSheetRepository.playersOnCourt(sheet,"a",80).first())
  val minutes=MatchSheetRepository.minutes(sheet,"a",90)
  assertEquals(75,minutes["p1"]);assertEquals(15,minutes["p6"]);assertEquals(450,minutes.values.sum())
 }
 @Test fun invalidOrOutOfOrderChangesAreRejected() {
  listOf(listOf(Substitution("a","p6","p1",20)),listOf(Substitution("a","p1","p2",20)),listOf(Substitution("a","p1","p6",65),Substitution("a","p6","p1",60))).forEach { changes ->
   assertTrue(runCatching { MatchSheetRepository.validate(initial.copy(substitutions=changes),fixture,players) }.isFailure)
  }
 }
 @Test fun outsidersDuplicateStartersAndExcessFormationAreRejected() {
  listOf(initial.copy(teams=listOf(TeamSheet("b","2-2",listOf("p1")))),initial.copy(teams=listOf(TeamSheet("a","2-2",listOf("p1","p1")))),initial.copy(teams=listOf(TeamSheet("a","2-2",players.map { it.id })))).forEach { sheet ->assertTrue(runCatching { MatchSheetRepository.validate(sheet,fixture,players) }.isFailure) }
 }
 @Test fun durationBeforeLastChangeIsRejected() {
  assertTrue(runCatching { MatchSheetRepository.minutes(initial.copy(substitutions=listOf(Substitution("a","p1","p6",80))),"a",70) }.isFailure)
 }
}
