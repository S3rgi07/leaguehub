package com.leaguehub.app.navigation.graphs
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.leaguehub.app.feature.local.LocalDependencies
import com.leaguehub.app.navigation.*
import com.leaguehub.app.feature.local.home.LocalHomeRoute
import com.leaguehub.app.feature.local.leagues.LeaguesRoute
import com.leaguehub.app.feature.local.clubs.ClubsRoute
import com.leaguehub.app.feature.local.players.LocalPlayersRoute
import com.leaguehub.app.feature.local.matches.MatchesRoute
import com.leaguehub.app.feature.local.events.LocalEventsRoute
import com.leaguehub.app.feature.local.standings.StandingsRoute
import com.leaguehub.app.feature.local.profile.LocalProfileRoute
import com.leaguehub.app.feature.local.backup.BackupRoute
fun NavGraphBuilder.localNavigation(nav:NavHostController,deps:LocalDependencies) {
 navigation<LocalHubDestination>(startDestination=LocalHomeDestination) {
  composable<LocalHomeDestination> { entry -> LocalHomeRoute(nav,deps,entry) }
  composable<LocalLeaguesDestination> { entry -> LeaguesRoute(nav,deps,entry) }
  composable<LocalClubsDestination> { entry -> ClubsRoute(nav,deps,entry) }
  composable<LocalPlayersDestination> { entry -> LocalPlayersRoute(nav,deps,entry) }
  composable<LocalMatchesDestination> { entry -> MatchesRoute(nav,deps,entry) }
  composable<LocalEventsDestination> { entry -> LocalEventsRoute(nav,deps,entry) }
  composable<LocalStandingsDestination> { entry -> StandingsRoute(nav,deps,entry) }
  composable<LocalProfileDestination> { entry -> LocalProfileRoute(nav,deps,entry) }
  composable<LocalBackupDestination> { entry -> BackupRoute(nav,deps,entry) }
 }
}
