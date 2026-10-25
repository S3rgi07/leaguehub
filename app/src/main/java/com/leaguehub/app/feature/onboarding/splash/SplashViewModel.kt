package com.leaguehub.app.feature.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashState())

    val state: StateFlow<SplashState> = _state.asStateFlow()

    private var initializationStarted = false

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.Initialize -> initialize()
        }
    }

    private fun initialize() {
        if (initializationStarted) return

        initializationStarted = true

        viewModelScope.launch {
            // Simulación de inicialización
            delay(2000)

            _state.update {
                it.copy(
                    isLoading = false,
                    isReady = true
                )
            }
        }
    }
}