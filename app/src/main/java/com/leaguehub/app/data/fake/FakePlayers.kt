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

val fakePlayers = listOf(
    sofia,
    PlayerUiModel("valeria_cruz", "Valeria Cruz", 8, "Mediocampo", falcons.id, 4, 10, 16, rating = 8.4, isStarter = true),
    PlayerUiModel("ana_mora", "Ana Mora", 1, "Portera", falcons.id, 0, 1, 16, rating = 7.9, isStarter = true)
) + (2..22).filter { it != 8 && it != 10 }.map { number ->
    PlayerUiModel(
        id = "falcons_player_$number", name = "Jugador $number", number = number,
        position = if (number < 6) "Defensa" else "Mediocampo", teamId = falcons.id,
        goals = 0, assists = 0, matchesPlayed = 0
    )
}
