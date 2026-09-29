package com.leaguehub.app.feature.player

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.falcons
import com.leaguehub.app.feature.home.HomeRoute
import com.leaguehub.app.feature.league.detail.LeagueDetailRoute
import com.leaguehub.app.feature.league.standings.StandingsRoute
import com.leaguehub.app.feature.matches.calendar.CalendarRoute
import com.leaguehub.app.feature.matches.detail.MatchDetailRoute
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.profile.ProfileRoute
import com.leaguehub.app.feature.team.detail.TeamDetailRoute

/** Small UI-only host. Routes can be plugged into the shared navigation graph later. */
@Composable
fun PlayerApp(onOpenMap: (MatchUiModel, Boolean) -> Unit, onShare: (String) -> Unit) {
    var backStack by rememberSaveable { mutableStateOf(listOf("HOME")) }
    var followedTeams by rememberSaveable { mutableStateOf(listOf(falcons.id)) }
    var dialogTitle by rememberSaveable { mutableStateOf<String?>(null) }
    var dialogBody by rememberSaveable { mutableStateOf("") }
    val holder = rememberSaveableStateHolder()
    val route = backStack.last()
    fun open(value: String) { if (route != value) backStack = backStack + value }
    val back: () -> Unit = { if (backStack.size > 1) backStack = backStack.dropLast(1) else backStack = listOf("HOME") }
    val navigate: (PlayerDestination) -> Unit = { backStack = listOf(it.name) }
    val matchClick: (MatchUiModel) -> Unit = { open("match:${it.id}") }
    val notifications: () -> Unit = {
        dialogTitle = "Notificaciones"
        dialogBody = "No tienes alertas nuevas. Aquí aparecerán las novedades de tu liga."
    }
    BackHandler(backStack.size > 1, back)
    holder.SaveableStateProvider(route) {
        when {
            route == "HOME" -> HomeRoute(navigate, matchClick, { open("league") }, notifications)
            route == "STANDINGS" -> StandingsRoute(navigate, { open("team:${it.id}") }, { open("league") })
            route == "CALENDAR" -> CalendarRoute(navigate, matchClick)
            route == "PROFILE" || route.startsWith("profile:") -> ProfileRoute(
                playerId = route.substringAfter(':', "sofia_mendez"), onNavigate = navigate,
                onTeamClick = { open("team:${it.id}") },
                onSettingsClick = {
                    dialogTitle = "Perfil de jugador"
                    dialogBody = "Las estadísticas corresponden a la temporada Apertura 2026. La edición del perfil estará disponible al conectar la cuenta."
                })
            route == "league" -> LeagueDetailRoute(
                onTeamClick = { open("team:${it.id}") },
                onNewsClick = { dialogTitle = it.title; dialogBody = it.body },
                onBack = back, onNotificationsClick = notifications, onNavigate = navigate)
            route.startsWith("match:") -> MatchDetailRoute(route.substringAfter(':'), back, onOpenMap,
                { onShare("${it.homeTeam.name} vs ${it.awayTeam.name}\n${it.date} · ${it.time}\n${it.venue}") }, navigate)
            route.startsWith("team:") -> {
                val id = route.substringAfter(':')
                TeamDetailRoute(id, id in followedTeams,
                    { followedTeams = if (id in followedTeams) followedTeams - id else followedTeams + id },
                    { open("profile:${it.id}") }, matchClick, back,
                    { onShare("Sigue a ${it.name} en LeagueHub · Liga Universitaria GT") }, navigate)
            }
        }
    }
    if (dialogTitle != null) AlertDialog(
        onDismissRequest = { dialogTitle = null },
        title = { Text(dialogTitle.orEmpty()) },
        text = { Text(dialogBody) },
        confirmButton = { TextButton({ dialogTitle = null }) { Text("Entendido") } }
    )
}
