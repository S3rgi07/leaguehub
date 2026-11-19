package com.leaguehub.app.data.repository

import com.leaguehub.app.data.local.*

data class TableRow(val club: ClubEntity, val played: Int, val won: Int, val drawn: Int, val lost: Int, val goalsFor: Int, val goalsAgainst: Int) {
    val points get() = won * 3 + drawn
    val difference get() = goalsFor - goalsAgainst
}
data class PlayerTotals(val goals: Int = 0, val assists: Int = 0, val matches: Int = 0, val yellow: Int = 0, val red: Int = 0, val minutes: Int = 0, val rating: Double? = null)

data class CompetitionSnapshot(
    val leagues: List<LeagueEntity> = emptyList(),
    val clubs: List<ClubEntity> = emptyList(),
    val players: List<PlayerEntity> = emptyList(),
    val fixtures: List<FixtureEntity> = emptyList(),
    val events: List<MatchEventEntity> = emptyList(),
    val sheets: Map<String,MatchSheet> = emptyMap()
) {
    fun table(leagueId: String): List<TableRow> = clubs.filter { it.leagueId == leagueId }.map { club ->
        val games = fixtures.filter { it.leagueId == leagueId && it.homeScore != null && it.awayScore != null && (it.homeId == club.id || it.awayId == club.id) }
        val scores = games.map { if (it.homeId == club.id) it.homeScore!! to it.awayScore!! else it.awayScore!! to it.homeScore!! }
        TableRow(club, games.size, scores.count { it.first > it.second }, scores.count { it.first == it.second }, scores.count { it.first < it.second }, scores.sumOf { it.first }, scores.sumOf { it.second })
    }.sortedWith(compareByDescending<TableRow> { it.points }.thenByDescending { it.difference }.thenByDescending { it.goalsFor }.thenBy { it.club.name.lowercase() }.thenBy { it.club.id })

    fun totals(playerId: String): PlayerTotals {
        val completed = fixtures.filter { it.homeScore != null && it.awayScore != null }.map { it.id }.toSet()
        val records = events.filter { it.playerId == playerId && it.fixtureId in completed }
        val ratings = records.filter { it.kind == "APPEARANCE" }.mapNotNull { it.rating }
        return PlayerTotals(records.count { it.kind == "GOAL" }, records.count { it.kind == "ASSIST" }, records.map { it.fixtureId }.distinct().size,
            records.count { it.kind == "YELLOW" }, records.count { it.kind == "RED" }, records.sumOf { it.minutesPlayed }, ratings.takeIf { it.isNotEmpty() }?.average())
    }
}
