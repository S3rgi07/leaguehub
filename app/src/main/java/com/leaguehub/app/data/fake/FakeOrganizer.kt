package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.organizer.model.*

val fakeMatchdaySummary = MatchdaySummaryUiModel(matches = 4, live = 1, pending = 3, published = 5, totalReports = 8)
val fakePendingReports = listOf(
    PendingReportUiModel("wolves_condores", "Wolves vs Cóndores", "Finalizó · hace 18 min", true),
    PendingReportUiModel("united_jaguars", "United vs Jaguars", "Finalizó · hace 42 min", false)
)
val fakeMatchManagement = MatchManagementUiModel(
    match = falconsVsTitans.copy(id = "falcons_titans_scheduled", status = MatchStatus.SCHEDULED,
        homeScore = null, awayScore = null, minute = null, date = "06 SEP", time = "19:30"),
    number = "32",
    referee = "Marco Salazar",
    rosters = listOf(TeamRosterUiModel(falcons, 11, 7, 0), TeamRosterUiModel(titans, 10, 7, 1))
)
