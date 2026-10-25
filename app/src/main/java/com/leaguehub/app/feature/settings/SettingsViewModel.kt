package com.leaguehub.app.feature.settings

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val sessionRepository: UserSessionRepository,
    private val followedTeamsRepository: FollowedTeamsRepository
) : ViewModel() {
    private val session = sessionRepository.session.value
    private val _state = MutableStateFlow(
        SettingsState(
            userName = session.userName?.takeIf { it.isNotBlank() } ?: "Sofía Méndez",
            userEmail = session.email?.takeIf { it.isNotBlank() } ?: "sofia@leaguehub.app"
        )
    )
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.AppearanceClicked -> _state.update {
                it.copy(appearance = if (it.appearance == "Oscuro") "Claro" else "Oscuro")
            }
            SettingsEvent.PrivacyClicked -> _state.update {
                it.copy(message = "Tus datos de esta demostración se mantienen localmente durante la sesión.")
            }
            SettingsEvent.LogoutClicked -> {
                sessionRepository.logout()
                followedTeamsRepository.clear()
                _state.update { it.copy(isLoggedOut = true) }
            }
            SettingsEvent.MessageDismissed -> _state.update { it.copy(message = null) }
        }
    }
}
