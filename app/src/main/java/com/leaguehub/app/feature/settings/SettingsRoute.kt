package com.leaguehub.app.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onOrganizerClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLogout: () -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SettingsViewModel(application.userSessionRepository, application.followedTeamsRepository)
            }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isLoggedOut) { if (state.isLoggedOut) onLogout() }
    SettingsScreen(
        userName = state.userName,
        userEmail = state.userEmail,
        leagueName = state.leagueName,
        activeNotifications = state.activeNotifications,
        appearance = state.appearance,
        onBackClick = onBack,
        onProfileClick = onProfileClick,
        onNotificationsClick = onNotificationsClick,
        onAppearanceClick = { viewModel.onEvent(SettingsEvent.AppearanceClicked) },
        onPrivacyClick = { viewModel.onEvent(SettingsEvent.PrivacyClicked) },
        onOrganizerClick = onOrganizerClick,
        onChangeLeagueClick = onExploreClick,
        onLogoutClick = { viewModel.onEvent(SettingsEvent.LogoutClicked) }
    )
    state.message?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(SettingsEvent.MessageDismissed) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(SettingsEvent.MessageDismissed) }) { Text("Entendido") }
            }
        )
    }
}

