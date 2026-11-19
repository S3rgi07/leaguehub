package com.leaguehub.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.leaguehub.app.navigation.graphs.localNavigation
import com.leaguehub.app.feature.local.rememberLocalDependencies
import androidx.navigation.compose.rememberNavController
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.navigation.graphs.authNavigation
import com.leaguehub.app.navigation.graphs.playerNavigation
import com.leaguehub.app.navigation.graphs.organizerNavigation

@Composable
fun LeagueHubNavHost(
    onOpenMap: (MatchUiModel, Boolean) -> Unit,
    onShare: (String) -> Unit
) {
    val navController = rememberNavController()
    val dependencies = rememberLocalDependencies()

    NavHost(
        navController = navController,
        startDestination = SplashDestination
    ) {
        authNavigation(navController)

        localNavigation(navController,dependencies)


    }
}
