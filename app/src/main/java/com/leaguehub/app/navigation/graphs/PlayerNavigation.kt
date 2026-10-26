package com.leaguehub.app.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.sofia
import com.leaguehub.app.data.fake.universityLeague
import com.leaguehub.app.feature.explore.ExploreCategory
import com.leaguehub.app.feature.explore.ExploreRoute
import com.leaguehub.app.feature.home.HomeRoute
import com.leaguehub.app.feature.league.detail.LeagueDetailRoute
import com.leaguehub.app.feature.league.standings.StandingsRoute
import com.leaguehub.app.feature.matches.calendar.CalendarRoute
import com.leaguehub.app.feature.matches.detail.MatchDetailRoute
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.notifications.NotificationsRoute
import com.leaguehub.app.feature.profile.ProfileRoute
import com.leaguehub.app.feature.settings.SettingsRoute
import com.leaguehub.app.feature.team.detail.TeamDetailRoute
import com.leaguehub.app.navigation.CalendarDestination
import com.leaguehub.app.navigation.ExploreDestination
import com.leaguehub.app.navigation.HomeDestination
import com.leaguehub.app.navigation.LeagueDetailDestination
import com.leaguehub.app.navigation.LoginDestination
import com.leaguehub.app.navigation.MatchDetailDestination
import com.leaguehub.app.navigation.NotificationsDestination
import com.leaguehub.app.navigation.OrganizerDashboardDestination
import com.leaguehub.app.navigation.PlayerProfileDestination
import com.leaguehub.app.navigation.SettingsDestination
import com.leaguehub.app.navigation.StandingsDestination
import com.leaguehub.app.navigation.TeamDetailDestination

fun NavGraphBuilder.playerNavigation(
    navController: NavHostController,
    onOpenMap: (MatchUiModel, Boolean) -> Unit,
    onShare: (String) -> Unit
) {
    val onBottomNavigation: (PlayerDestination) -> Unit = { destination ->
        val route: Any = when (destination) {
            PlayerDestination.HOME -> HomeDestination
            PlayerDestination.STANDINGS -> StandingsDestination
            PlayerDestination.CALENDAR -> CalendarDestination
            PlayerDestination.PROFILE -> PlayerProfileDestination(sofia.id)
        }
        navController.navigate(route) {
            popUpTo<HomeDestination> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    composable<HomeDestination> {
        HomeRoute(
            onNavigate = onBottomNavigation,
            onMatchClick = { navController.navigate(MatchDetailDestination(it.id)) },
            onLeagueClick = { navController.navigate(LeagueDetailDestination(universityLeague.id)) },
            onNotificationsClick = { navController.navigate(NotificationsDestination) }
        )
    }

    composable<StandingsDestination> {
        StandingsRoute(
            onNavigate = onBottomNavigation,
            onTeamClick = { navController.navigate(TeamDetailDestination(it.id)) },
            onLeagueClick = { navController.navigate(LeagueDetailDestination(universityLeague.id)) }
        )
    }

    composable<PlayerProfileDestination> { entry ->
        val destination = entry.toRoute<PlayerProfileDestination>()
        ProfileRoute(
            playerId = destination.playerId,
            onNavigate = onBottomNavigation,
            onTeamClick = { navController.navigate(TeamDetailDestination(it.id)) },
            onSettingsClick = { navController.navigate(SettingsDestination) }
        )
    }

    composable<CalendarDestination> {
        CalendarRoute(
            onNavigate = onBottomNavigation,
            onMatchClick = { navController.navigate(MatchDetailDestination(it.id)) }
        )
    }

    composable<MatchDetailDestination> { entry ->
        val destination = entry.toRoute<MatchDetailDestination>()
        MatchDetailRoute(
            matchId = destination.matchId,
            onBack = { navController.popBackStack() },
            onOpenMap = onOpenMap,
            onShare = { match ->
                onShare("${match.homeTeam.name} vs ${match.awayTeam.name}\n${match.date} · ${match.time}\n${match.venue}")
            },
            onNavigate = onBottomNavigation
        )
    }

    composable<LeagueDetailDestination> { entry ->
        val destination = entry.toRoute<LeagueDetailDestination>()
        LeagueDetailRoute(
            leagueId = destination.leagueId,
            onTeamClick = { navController.navigate(TeamDetailDestination(it.id)) },
            onBack = { navController.popBackStack() },
            onNotificationsClick = { navController.navigate(NotificationsDestination) },
            onNavigate = onBottomNavigation
        )
    }

    composable<TeamDetailDestination> { entry ->
        val destination = entry.toRoute<TeamDetailDestination>()
        TeamDetailRoute(
            teamId = destination.teamId,
            onPlayerClick = { navController.navigate(PlayerProfileDestination(it.id)) },
            onMatchClick = { navController.navigate(MatchDetailDestination(it.id)) },
            onBack = { navController.popBackStack() },
            onShare = { onShare("Sigue a ${it.name} en LeagueHub · Liga Universitaria GT") },
            onNavigate = onBottomNavigation
        )
    }

    composable<NotificationsDestination> {
        NotificationsRoute(onBack = { navController.popBackStack() })
    }

    composable<SettingsDestination> {
        SettingsRoute(
            onBack = { navController.popBackStack() },
            onProfileClick = { navController.navigate(PlayerProfileDestination(sofia.id)) },
            onNotificationsClick = { navController.navigate(NotificationsDestination) },
            onOrganizerClick = { navController.navigate(OrganizerDashboardDestination) },
            onExploreClick = { navController.navigate(ExploreDestination) },
            onLogout = {
                navController.navigate(LoginDestination) {
                    popUpTo<HomeDestination> { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    composable<ExploreDestination> {
        ExploreRoute(
            onItemClick = { item ->
                when (item.category) {
                    ExploreCategory.LEAGUES -> navController.navigate(LeagueDetailDestination(item.id))
                    ExploreCategory.TEAMS -> navController.navigate(TeamDetailDestination(item.id))
                    ExploreCategory.PLAYERS -> navController.navigate(PlayerProfileDestination(item.id))
                    ExploreCategory.ALL -> Unit
                }
            },
            onBack = { navController.popBackStack() }
        )
    }
}
