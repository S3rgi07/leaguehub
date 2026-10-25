package com.leaguehub.app.feature.onboarding.role

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RoleSelectionViewModel : ViewModel() {

    private val _state = MutableStateFlow(RoleSelectionState())

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
                _state.update {
                    it.copy(
                        isSelectionConfirmed = true
                    )
                }
            }
        }
    }
}