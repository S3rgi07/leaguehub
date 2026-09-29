package com.leaguehub.app.feature.team.model

import androidx.compose.ui.graphics.Color

data class TeamUiModel(
    val id: String,
    val name: String,
    val abbreviation: String,
    val color: Color,
    val foundedYear: Int? = null,
    val location: String = "",
    val squadSize: Int = 0
)
