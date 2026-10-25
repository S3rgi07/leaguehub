package com.leaguehub.app.feature.onboarding.role

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoleSelectionViewModel(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

    private val initialRole = when (
        sessionRepository.session.value.role
    ) {
        SessionRole.ORGANIZER -> UserRole.ORGANIZER
        else -> UserRole.PLAYER
    }

    private val _state = MutableStateFlow(
        RoleSelectionState(selectedRole = initialRole)
    )

    val state: StateFlow<RoleSelectionState> = _state.asStateFlow()

    fun onEvent(event: RoleSelectionEvent) {
        when (event) {

            is RoleSelectionEvent.RoleSelected -> {
                _state.update {
                    it.copy(
                        selectedRole = event.role,
                        isSelectionConfirmed = false
                    )
                }
            }

            RoleSelectionEvent.ContinueClicked -> {
                val role = when (_state.value.selectedRole) {
                    UserRole.PLAYER -> SessionRole.PLAYER
                    UserRole.ORGANIZER -> SessionRole.ORGANIZER
                }

                sessionRepository.selectRole(role)

                _state.update {
                    it.copy(isSelectionConfirmed = true)
                }
            }
        }
    }
}