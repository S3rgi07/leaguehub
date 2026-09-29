package com.leaguehub.app.data.fake

import androidx.compose.ui.graphics.Color
import com.leaguehub.app.feature.team.model.TeamUiModel

val falcons = TeamUiModel(
    id = "falcons",
    name = "Falcons",
    abbreviation = "FAL",
    color = Color(0xFF1687D4),
    foundedYear = 2018,
    location = "Zona 12",
    squadSize = 22
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

val deportivoSur = TeamUiModel("deportivo_sur", "Deportivo Sur", "SUR", Color(0xFF3289AD))
val racing12 = TeamUiModel("racing_12", "Racing 12", "R12", Color(0xFF587FE7))
val leones = TeamUiModel("leones", "Leones", "LEO", Color(0xFFE8752E))
val atlas = TeamUiModel("atlas", "Atlas", "ATL", Color(0xFFEE7A35))
val jaguares = TeamUiModel("jaguares", "Jaguares", "JAG", Color(0xFFEAB748))
val aurora = TeamUiModel("aurora", "Aurora", "AUR", Color(0xFFCF659F))
val realCampus = TeamUiModel("real_campus", "Real Campus", "REA", Color(0xFF4F9C87))

val fakeTeams = listOf(
    falcons,
    titans,
    wolves,
    atleticoNova,
    campusUnited, deportivoSur, racing12, leones, atlas, jaguares, aurora, realCampus
)
