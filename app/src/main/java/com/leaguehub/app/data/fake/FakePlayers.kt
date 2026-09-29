package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.profile.model.PlayerUiModel

val sofia = PlayerUiModel(
    id = "sofia_mendez",
    name = "Sofía Méndez",
    number = 10,
    position = "Delantera",
    teamId = falcons.id,
    goals = 12,
    assists = 8,
    matchesPlayed = 16,
    yellowCards = 3,
    minutesPlayed = 1284,
    recentRatings = listOf(8.1, 7.4, 8.8, 8.3, 9.0),
    rating = 9.0,
    isStarter = true,
    isCaptain = true,
    scoringStreak = 4,
    mvpAwards = 2,
    goalRank = 3
)

private fun squadPlayer(id: String, name: String, number: Int, position: String, teamId: String) =
    PlayerUiModel(id, name, number, position, teamId, goals = 0, assists = 0, matchesPlayed = 8)

val fakePlayers = listOf(
    squadPlayer("fal_mora", "Mora", 1, "Portera", falcons.id),
    squadPlayer("fal_leon", "León", 2, "Defensa", falcons.id),
    squadPlayer("fal_vega", "Vega", 4, "Defensa", falcons.id),
    squadPlayer("fal_sol", "Sol", 5, "Defensa", falcons.id),
    squadPlayer("fal_gil", "Gil", 3, "Defensa", falcons.id),
    squadPlayer("fal_cruz", "V. Cruz", 6, "Mediocampista", falcons.id),
    squadPlayer("fal_reyes", "Reyes", 8, "Mediocampista", falcons.id),
    sofia,
    squadPlayer("fal_paz", "Paz", 7, "Delantera", falcons.id),
    squadPlayer("fal_navas", "Navas", 9, "Delantera", falcons.id),
    squadPlayer("fal_luna", "Luna", 11, "Delantera", falcons.id),
    squadPlayer("fal_rios", "Ríos", 12, "Portera", falcons.id),
    squadPlayer("fal_soto", "Soto", 14, "Defensa", falcons.id),
    squadPlayer("fal_diaz", "Díaz", 15, "Mediocampista", falcons.id),
    squadPlayer("fal_rojas", "Rojas", 16, "Defensa", falcons.id),
    squadPlayer("fal_ortiz", "Ortiz", 17, "Delantera", falcons.id),
    squadPlayer("fal_arias", "Arias", 18, "Mediocampista", falcons.id),
    squadPlayer("fal_salas", "Salas", 19, "Delantera", falcons.id),
    squadPlayer("tit_mendez", "Méndez", 1, "Portero", titans.id),
    squadPlayer("tit_ruiz", "D. Ruiz", 2, "Defensa", titans.id),
    squadPlayer("tit_perez", "L. Pérez", 4, "Defensa", titans.id),
    squadPlayer("tit_diaz", "M. Díaz", 5, "Defensa", titans.id),
    squadPlayer("tit_gomez", "Gómez", 3, "Defensa", titans.id),
    squadPlayer("tit_castro", "Castro", 6, "Mediocampista", titans.id),
    squadPlayer("tit_sosa", "Sosa", 8, "Mediocampista", titans.id),
    squadPlayer("tit_lopez", "A. López", 10, "Mediocampista", titans.id),
    squadPlayer("tit_ortiz", "Ortiz", 7, "Delantero", titans.id),
    squadPlayer("tit_ramirez", "Ramírez", 9, "Delantero", titans.id),
    squadPlayer("tit_ramos", "Ramos", 11, "Delantero", titans.id),
    squadPlayer("tit_leal", "Leal", 12, "Portero", titans.id),
    squadPlayer("tit_paz", "Paz", 14, "Defensa", titans.id),
    squadPlayer("tit_roca", "Roca", 15, "Mediocampista", titans.id),
    squadPlayer("tit_mena", "Mena", 16, "Defensa", titans.id),
    squadPlayer("tit_solis", "Solís", 17, "Delantero", titans.id),
    squadPlayer("tit_cano", "Cano", 18, "Mediocampista", titans.id),
    squadPlayer("tit_ayala", "Ayala", 19, "Delantero", titans.id)
)
