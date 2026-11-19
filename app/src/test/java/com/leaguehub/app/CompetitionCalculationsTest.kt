package com.leaguehub.app

import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import org.junit.Assert.*
import org.junit.Test

class CompetitionCalculationsTest {
    private val clubs=listOf(ClubEntity("a","league","Atlas"),ClubEntity("b","league","Bravos"),ClubEntity("c","league","Cóndores"))
    private fun fixture(id: String, home: String, away: String, hs: Int?, aws: Int?)=FixtureEntity(id,"league",home,away,"2026-11-20T16:00","Cancha",1,hs,aws)
    @Test fun correctingResultsRecalculatesWithoutDoubleCountingAndScheduledGamesDoNotCount() {
        val data=CompetitionSnapshot(clubs=clubs,fixtures=listOf(fixture("one","a","b",2,0),fixture("two","a","c",null,null),fixture("three","b","c",1,1)))
        val table=data.table("league")
        assertEquals(listOf("a","c","b"),table.map { it.club.id })
        assertEquals(3,table.first().points)
        assertEquals(1,table.first().played)
        val corrected=data.copy(fixtures=data.fixtures.map { if(it.id=="one") it.copy(homeScore=0,awayScore=1) else it }).table("league")
        assertEquals("b",corrected.first().club.id)
        assertEquals(4,corrected.first().points)
        assertEquals(2,corrected.first().played)
        assertEquals(0,corrected.first { it.club.id=="a" }.points)
    }
    @Test fun playerTotalsUseOnlyCompletedGamesAndUniqueMatches() {
        val completed=fixture("one","a","b",2,0)
        val pending=fixture("two","a","b",null,null)
        val events=listOf(MatchEventEntity("1","one","p","GOAL",5),MatchEventEntity("2","one","p","GOAL",20),
            MatchEventEntity("3","one","p","APPEARANCE",0,90,8.0),MatchEventEntity("4","two","p","GOAL",10))
        val data=CompetitionSnapshot(fixtures=listOf(completed,pending),events=events)
        assertEquals(PlayerTotals(goals=2,matches=1,minutes=90,rating=8.0),data.totals("p"))
        assertEquals(PlayerTotals(),data.copy(fixtures=listOf(pending)).totals("p"))
    }
}
