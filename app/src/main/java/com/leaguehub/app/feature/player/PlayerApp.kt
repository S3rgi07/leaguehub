package com.leaguehub.app.feature.player

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.navigation.HomeDestination
import com.leaguehub.app.navigation.graphs.playerNavigation

/** Host independiente para previsualizar y probar todo el flujo del jugador. */
@Composable
fun PlayerApp(
    onOpenMap: (MatchUiModel, Boolean) -> Unit,
    onShare: (String) -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeDestination) {
        playerNavigation(navController, onOpenMap, onShare)
    }
}
