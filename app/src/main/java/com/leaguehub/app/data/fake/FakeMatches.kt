package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.matches.model.MatchUiModel

val falconsVsTitans = MatchUiModel(
    id = "falcons_titans_j8",
    homeTeam = falcons,
    awayTeam = titans,
    homeScore = 2,
    awayScore = 1,
    status = MatchStatus.LIVE,
    minute = "75'",
    matchday = 8,
    date = "13 SEP",
    time = "16:00",
    venue = "Cancha Central USAC"
)

val falconsVsWolves = MatchUiModel(
    id = "falcons_wolves_j9",
    homeTeam = falcons,
    awayTeam = wolves,
    status = MatchStatus.SCHEDULED,
    matchday = 9,
    date = "20 SEP",
    time = "11:30",
    venue = "Campo Central"
)

val fakeMatches = listOf(
    falconsVsTitans,
    falconsVsWolves
)