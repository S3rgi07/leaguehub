package com.leaguehub.app.feature.onboarding.permissions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.leaguehub.app.data.repository.UserSessionRepository

class PermissionsViewModel(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionsState())

    val state: StateFlow<PermissionsState> = _state.asStateFlow()

    fun onEvent(event: PermissionsEvent) {
        when (event) {

            is PermissionsEvent.LiveMatchesChanged -> {
                _state.update {
                    it.copy(liveMatchesEnabled = event.enabled)
                }
            }

            is PermissionsEvent.RemindersChanged -> {
                _state.update {
                    it.copy(remindersEnabled = event.enabled)
                }
            }

            is PermissionsEvent.SmartPreviewChanged -> {
                _state.update {
                    it.copy(smartPreviewEnabled = event.enabled)
                }
            }

            is PermissionsEvent.LocationChanged -> {
                _state.update {
                    it.copy(locationEnabled = event.enabled)
                }
            }

            PermissionsEvent.CompleteClicked -> {
                sessionRepository.completeOnboarding()

                _state.update {
                    it.copy(
                        isCompleted = true,
                        isSkipped = false
                    )
                }
            }

            PermissionsEvent.SkipClicked -> {
                _state.update {
                    it.copy(
                        isSkipped = true,
                        isCompleted = false
                    )
                }
            }
        }
    }
}