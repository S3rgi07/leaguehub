package com.leaguehub.app.feature.explore

import com.leaguehub.app.R

data class ExploreState(
    val searchQuery: String = "",
    val selectedCategory: ExploreCategory = ExploreCategory.ALL,
    val featuredLeagues: List<ExploreItemUiModel> = defaultExploreLeagues,
    val popularTeams: List<ExploreItemUiModel> = defaultExploreTeams,
    val suggestedPlayers: List<ExploreItemUiModel> = defaultExplorePlayers
)

val defaultExploreLeagues = listOf(
    ExploreItemUiModel("university_league_gt", "Liga Universitaria GT", "12 equipos · Jornada 8", ExploreCategory.LEAGUES, R.drawable.lh_league, "EN CURSO"),
    ExploreItemUiModel("metropolitana", "Liga Metropolitana", "16 equipos · Apertura 2026", ExploreCategory.LEAGUES, R.drawable.lh_league)
)
val defaultExploreTeams = listOf(
    ExploreItemUiModel("falcons", "Falcons", "Liga Universitaria GT · 2.º lugar", ExploreCategory.TEAMS, R.drawable.lh_shield),
    ExploreItemUiModel("titans", "Titans", "Liga Universitaria GT · 3.º lugar", ExploreCategory.TEAMS, R.drawable.lh_shield)
)
val defaultExplorePlayers = listOf(
    ExploreItemUiModel("sofia_mendez", "Sofía Méndez", "Falcons · Delantera · #10", ExploreCategory.PLAYERS, R.drawable.lh_avatar, "12 GOLES")
)
