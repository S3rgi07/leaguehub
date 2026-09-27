package com.leaguehub.app.data.fake

import androidx.compose.ui.graphics.Color
import com.leaguehub.app.feature.team.model.TeamUiModel

val falcons = TeamUiModel(
    id = "falcons",
    name = "Falcons",
    abbreviation = "FAL",
    color = Color(0xFF1687D4)
)

val titans = TeamUiModel(
    id = "titans",
    name = "Titans",
    abbreviation = "TIT",
    color = Color(0xFFE8752E)
)

val wolves = TeamUiModel(
    id = "wolves",
    name = "Wolves FC",
    abbreviation = "WOL",
    color = Color(0xFF7C5CFC)
)

val atleticoNova = TeamUiModel(
    id = "atletico_nova",
    name = "Atlético Nova",
    abbreviation = "NOV",
    color = Color(0xFFE05263)
)

val campusUnited = TeamUiModel(
    id = "campus_united",
    name = "Campus United",
    abbreviation = "CAM",
    color = Color(0xFF26A69A)
)

val fakeTeams = listOf(
    falcons,
    titans,
    wolves,
    atleticoNova,
    campusUnited
)