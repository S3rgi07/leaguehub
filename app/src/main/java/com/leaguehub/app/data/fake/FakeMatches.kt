package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.matches.model.VenueUiModel
import java.time.LocalDate

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
    venue = "Cancha Central USAC",
    localDate = LocalDate.of(2026, 9, 13)
)

val falconsVsWolves = MatchUiModel(
    id = "falcons_wolves_j9",
    homeTeam = falcons,
    awayTeam = wolves,
    status = MatchStatus.SCHEDULED,
    matchday = 9,
    date = "20 SEP",
    time = "11:30",
    venue = "Campo Central",
    localDate = LocalDate.of(2026, 9, 20)
)

val fakeMatches = listOf(
    falconsVsTitans,
    falconsVsWolves
)

val centralVenue = VenueUiModel(
    address = "Zona 12, Ciudad de Guatemala", accessTime = "15:15",
    travelTime = "12 min", distance = "3.4 km",
    arrivalAdvice = "Llega 30 min antes · estacionamiento limitado",
    mapQuery = "Cancha Central USAC, Zona 12, Ciudad de Guatemala"
)

// P01/P08/P09 use the scheduled snapshot; Match Center keeps its existing live fixture.
val upcomingFalconsVsTitans = falconsVsTitans.copy(
    homeScore = null, awayScore = null, status = MatchStatus.SCHEDULED, minute = null,
    countdown = "02d 14h 36m", venueDetails = centralVenue
)
val campusVsFalcons = falconsVsWolves.copy(
    id = "campus_falcons_j9", homeTeam = campusUnited, awayTeam = falcons,
    venue = "Cancha Campus", venueDetails = null
)
val falconsVsNova = MatchUiModel(
    "falcons_nova_j7", falcons, atleticoNova, 2, 2, MatchStatus.FINISHED,
    matchday = 7, date = "5 SEP", time = "16:00", venue = "Cancha Central USAC",
    localDate = LocalDate.of(2026, 9, 5)
)
val wolvesVsAtlas = MatchUiModel(
    "wolves_atlas_j7", wolves, atlas, 3, 1, MatchStatus.FINISHED,
    matchday = 7, date = "5 SEP", time = "11:30", venue = "Campo Norte",
    localDate = LocalDate.of(2026, 9, 5)
)
val falconsVsLeones = upcomingFalconsVsTitans.copy(
    id = "falcons_leones_j10", awayTeam = leones, matchday = 10, date = "27 SEP",
    localDate = LocalDate.of(2026, 9, 27), countdown = null
)
val fakeRecentResults = listOf(wolvesVsAtlas, falconsVsNova)
val fakePlayerMatches = listOf(upcomingFalconsVsTitans, campusVsFalcons, falconsVsNova, falconsVsLeones)
