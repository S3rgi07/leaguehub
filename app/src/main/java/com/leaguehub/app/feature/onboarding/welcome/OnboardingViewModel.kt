package com.leaguehub.app.feature.onboarding.welcome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class OnboardingAction {
    NONE,
    START,
    LOGIN
}

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())

    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {

            OnboardingEvent.StartClicked -> {
                _state.update {
                    it.copy(
                        navigationAction = OnboardingAction.START
                    )
                }
            }

            OnboardingEvent.LoginClicked -> {
                _state.update {
                    it.copy(
                        navigationAction = OnboardingAction.LOGIN
                    )
                }
            }

            OnboardingEvent.NavigationHandled -> {
                _state.update {
                    it.copy(navigationAction = OnboardingAction.NONE)
                }
            }
        }
    }
}