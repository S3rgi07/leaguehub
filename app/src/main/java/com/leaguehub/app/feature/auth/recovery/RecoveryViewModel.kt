package com.leaguehub.app.feature.auth.recovery

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecoveryViewModel : ViewModel() {

    private val _state = MutableStateFlow(RecoveryState())

    val state: StateFlow<RecoveryState> = _state.asStateFlow()

    fun onEvent(event: RecoveryEvent) {
        when (event) {

            is RecoveryEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        errorMessage = null
                    )
                }
            }

            RecoveryEvent.SendCodeClicked -> {
                sendCode()
            }
        }
    }

    private fun sendCode() {
        val currentState = _state.value

        if (currentState.isLoading || currentState.codeSent) return

        if (currentState.email.isBlank()) {
            _state.update {
                it.copy(
                    errorMessage = "Ingresa tu correo electrónico"
                )
            }
            return
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(currentState.email.trim())
                .matches()
        ) {
            _state.update {
                it.copy(
                    errorMessage = "Ingresa un correo electrónico válido"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            // Simulación del envío del código
            delay(1200)

            _state.update {
                it.copy(
                    isLoading = false,
                    codeSent = true
                )
            }
        }
    }
}