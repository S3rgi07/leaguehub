package com.leaguehub.app.feature.profile
import com.leaguehub.app.data.session.UserSession
import com.leaguehub.app.feature.profile.model.PlayerUiModel

fun UserSession.toPlayer() = PlayerUiModel(
    id = userId.orEmpty(), name = userName?.takeIf { it.isNotBlank() } ?: "Mi perfil",
    number = 0, position = "Sin posición", teamId = "", goals = 0, assists = 0, matchesPlayed = 0
)