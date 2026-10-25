package com.leaguehub.app.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leaguehub.app.feature.home.HomeRoute
import com.leaguehub.app.navigation.HomeDestination
import com.leaguehub.app.core.components.PlayerDestination

fun NavGraphBuilder.playerNavigation(
    onNavigate: (PlayerDestination) -> Unit = {}
) {
    composable<HomeDestination> {
        HomeRoute(
            onNavigate = onNavigate,
            onMatchClick = {},
            onLeagueClick = {},
            onNotificationsClick = {}
        )
    }
}