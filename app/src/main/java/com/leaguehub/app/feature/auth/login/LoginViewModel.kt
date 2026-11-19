
package com.leaguehub.app.feature.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: com.leaguehub.app.data.repository.AuthRepository
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

            val result = try {
                authRepository.login(currentState.email, currentState.password)
                Result.success(Unit)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Result.failure<Unit>(error)
            }
            val success = result.isSuccess
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccessful = success,
                    errorMessage = if (success) {
                        null
                    } else {
                        result.exceptionOrNull()?.let { if (it is IllegalArgumentException) it.message else null } ?: "No se pudo iniciar sesión. Inténtalo de nuevo."
                    }
                )
            }
        }
    }
}
