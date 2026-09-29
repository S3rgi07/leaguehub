package com.leaguehub.app.feature.organizer.model

import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

data class PendingReportUiModel(val id: String, val title: String, val subtitle: String, val urgent: Boolean)
data class MatchdaySummaryUiModel(val matches: Int, val live: Int, val pending: Int, val published: Int, val totalReports: Int)
data class TeamRosterUiModel(val team: TeamUiModel, val starters: Int, val substitutes: Int, val missing: Int)
data class MatchManagementUiModel(val match: MatchUiModel, val number: String, val referee: String, val rosters: List<TeamRosterUiModel>)
enum class ScanStatus { SEARCHING, DETECTED, PROCESSING, ERROR }
