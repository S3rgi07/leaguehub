package com.leaguehub.app.feature.onboarding.permissions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.leaguehub.app.data.repository.UserSessionRepository

class PermissionsViewModel(
    private val sessionRepository: UserSessionRepository,
    private val storage: com.leaguehub.app.data.repository.UserScopedState<PermissionsState>? = null
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionsState())

    val state: StateFlow<PermissionsState> = storage?.state ?: _state.asStateFlow()
    private fun change(transform: (PermissionsState) -> PermissionsState) {
        storage?.update(transform) ?: _state.update(transform)
    }

    fun onEvent(event: PermissionsEvent) {
        when (event) {

            is PermissionsEvent.LiveMatchesChanged -> {
                change {
                    it.copy(liveMatchesEnabled = event.enabled)
                }
            }

            is PermissionsEvent.RemindersChanged -> {
                change {
                    it.copy(remindersEnabled = event.enabled)
                }
            }

            is PermissionsEvent.SmartPreviewChanged -> {
                change {
                    it.copy(smartPreviewEnabled = event.enabled)
                }
            }

            is PermissionsEvent.LocationChanged -> {
                change {
                    it.copy(locationEnabled = event.enabled)
                }
            }

            PermissionsEvent.CompleteClicked -> {
                sessionRepository.completeOnboarding()

                change {
                    it.copy(
                        isCompleted = true,
                        isSkipped = false
                    )
                }
            }

            PermissionsEvent.SkipClicked -> {
                change {
                    it.copy(
                        isSkipped = true,
                        isCompleted = false
                    )
                }
            }
        }
    }
}