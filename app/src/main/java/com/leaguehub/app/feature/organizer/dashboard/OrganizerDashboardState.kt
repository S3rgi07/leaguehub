package com.leaguehub.app.feature.organizer.dashboard

import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.organizer.model.MatchdaySummaryUiModel
import com.leaguehub.app.feature.organizer.model.PendingReportUiModel

data class OrganizerDashboardState(
    val leagueName: String = universityLeague.name,
    val match: MatchUiModel = falconsVsTitans,
    val summary: MatchdaySummaryUiModel = fakeMatchdaySummary,
    val pendingReports: List<PendingReportUiModel> = fakePendingReports,
    val selectedReportId: String? = null
)
