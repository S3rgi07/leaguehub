
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
            LoginEvent.ForgotPasswordClicked -> _state.update { it.copy(navigationAction=LoginAction.RECOVERY) }
            LoginEvent.CreateAccountClicked -> _state.update { it.copy(navigationAction=LoginAction.REGISTER) }
            LoginEvent.NavigationHandled -> _state.update { it.copy(navigationAction=LoginAction.NONE) }


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
                val session = authRepository.login(currentState.email, currentState.password)
                Result.success(if(session.role==null) LoginTarget.ROLE else LoginTarget.LOCAL)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Result.failure<LoginTarget>(error)
            }
            val success = result.isSuccess
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoginSuccessful = success,
                    target = result.getOrNull(),
                    password = if(success) "" else it.password,
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
