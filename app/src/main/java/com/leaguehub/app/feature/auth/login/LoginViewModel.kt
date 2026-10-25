package com.leaguehub.app.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {

            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        errorMessage = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        errorMessage = null
                    )
                }
            }

            LoginEvent.TogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        passwordVisible = !it.passwordVisible
                    )
                }
            }

            LoginEvent.LoginClicked -> {
                login()
            }
        }
    }

    private fun login() {
        val currentState = _state.value

        if (currentState.isLoading || currentState.isLoginSuccessful) return

        if (currentState.email.isBlank() ||
            currentState.password.isBlank()
        ) {
            _state.update {
                it.copy(
                    errorMessage = "Completa todos los campos"
                )
            }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(currentState.email.trim())
                .matches()
        ) {
            _state.update {
                it.copy(
                    errorMessage = "Ingresa un correo válido"
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

            // Simulación de una petición de autenticación
            delay(1000)

            val success =
                currentState.email.trim().equals(
                    "sofia@leaguehub.app",
                    ignoreCase = true
                ) && currentState.password == "LeagueHub123"

            _state.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccessful = success,
                    errorMessage = if (success) {
                        null
                    } else {
                        "Correo o contraseña incorrectos"
                    }
                )
            }
        }
    }
}