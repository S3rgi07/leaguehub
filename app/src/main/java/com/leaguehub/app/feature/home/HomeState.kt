package com.leaguehub.app.feature.home

import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.home.model.MatchPredictionUiModel
import com.leaguehub.app.feature.league.model.LeagueUiModel
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.profile.model.PlayerUiModel

data class HomeState(
    val player: PlayerUiModel = sofia,
    val league: LeagueUiModel = universityLeague,
    val nextMatch: MatchUiModel? = upcomingFalconsVsTitans,
    val recentResults: List<MatchUiModel> = fakeRecentResults,
    val prediction: MatchPredictionUiModel? = fakePrediction,
    val isRefreshing: Boolean = false
)
