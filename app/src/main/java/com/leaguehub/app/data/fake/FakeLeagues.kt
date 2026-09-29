package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.league.model.LeagueUiModel

val universityLeague = LeagueUiModel(
    id = "university_league_gt",
    name = "Liga Universitaria GT",
    season = "Apertura · 2026",
    currentMatchday = 8,
    totalMatchdays = 14,
    teamCount = 12,
    playerCount = 148,
    matchCount = 28
)

val fakeLeagues = listOf(
    universityLeague
)
