package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.matches.matchcenter.model.*

val fakeMatchEvents = listOf(
    MatchEventUiModel("yellow_75", "75′", "Tarjeta amarilla", "D. Ruiz · Titans", MatchEventType.YELLOW_CARD, false),
    MatchEventUiModel("goal_62", "62′", "GOOOL 2–1", "Sofía Méndez · Falcons", MatchEventType.GOAL, true),
    MatchEventUiModel("sub_55", "55′", "Sustitución", "L. Pérez por M. Díaz", MatchEventType.SUBSTITUTION, false),
    MatchEventUiModel("goal_41", "41′", "Gol 1–1", "A. López · Titans", MatchEventType.GOAL, false),
    MatchEventUiModel("goal_18", "18′", "Gol 1–0", "V. Cruz · Falcons", MatchEventType.GOAL, true)
)

val fakeMatchStatistics = listOf(
    MatchStatisticUiModel("Posesión", 58, 42),
    MatchStatisticUiModel("Tiros", 14, 9),
    MatchStatisticUiModel("Al arco", 7, 4),
    MatchStatisticUiModel("Faltas", 8, 12),
    MatchStatisticUiModel("Córners", 6, 3)
)

val fakeMomentum = listOf(.22f, .35f, .47f, .38f, .24f, .18f, .29f, .65f, .86f, .92f, .78f, .56f, .42f, .44f, .57f, .78f)

private val pitchPositions = listOf(
    .10f to .50f, .28f to .24f, .28f to .42f, .28f to .60f, .28f to .78f,
    .53f to .29f, .53f to .51f, .53f to .73f,
    .82f to .29f, .82f to .51f, .82f to .73f
)

val fakeLineups = listOf(falcons, titans).map { team ->
    val players = fakePlayers.filter { it.teamId == team.id }
    TeamLineupUiModel(
        team = team,
        formation = "4–3–3",
        coach = if (team.id == falcons.id) "R. Silva" else "M. Torres",
        starters = players.take(11).mapIndexed { index, player ->
            LineupPositionUiModel(player, pitchPositions[index].first, pitchPositions[index].second)
        },
        substitutes = players.drop(11)
    )
}
