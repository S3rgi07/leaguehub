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
    matchesPlayed = 16
)

val fakePlayers = listOf(
    sofia
)