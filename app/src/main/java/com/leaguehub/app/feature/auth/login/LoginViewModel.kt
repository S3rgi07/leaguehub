
package com.leaguehub.app.feature.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.UserSessionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {

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

        if (currentState.isLoading ||
            currentState.isLoginSuccessful
        ) return

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

        if (!Patterns.EMAIL_ADDRESS
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

            // Simulación de autenticación
            delay(1000)

            val email = currentState.email.trim()

            val success =
                email.equals(
                    "sofia@leaguehub.app",
                    ignoreCase = true
                ) &&
                        currentState.password == "LeagueHub123"

            if (success) {
                sessionRepository.login(
                    userId = "user_sofia_001",
                    userName = "Sofía Méndez",
                    email = email
                )

                android.util.Log.d(
                    "LeagueHubSession",
                    "Usuario autenticado: ${sessionRepository.session.value.email}"
                )
            }

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
