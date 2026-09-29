package com.leaguehub.app.feature.matches.matchcenter.model

import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel

enum class MatchCenterTab(val label: String) {
    SUMMARY("Resumen"), LINEUP("Alineación"), STATS("Estadísticas")
}

enum class MatchEventType { GOAL, YELLOW_CARD, SUBSTITUTION }

data class MatchEventUiModel(
    val id: String,
    val minute: String,
    val title: String,
    val description: String,
    val type: MatchEventType,
    val isHome: Boolean
)

data class MatchStatisticUiModel(val label: String, val home: Int, val away: Int)

/** Coordinates are fractions of the pitch, independent of the device size. */
data class LineupPositionUiModel(val player: PlayerUiModel, val x: Float, val y: Float)

data class TeamLineupUiModel(
    val team: TeamUiModel,
    val formation: String,
    val coach: String,
    val starters: List<LineupPositionUiModel>,
    val substitutes: List<PlayerUiModel>
)
