package com.leaguehub.app.feature.profile.model

data class PlayerUiModel(
    val id: String,
    val name: String,
    val number: Int,
    val position: String,
    val teamId: String,
    val goals: Int,
    val assists: Int,
    val matchesPlayed: Int
)