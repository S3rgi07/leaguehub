package com.leaguehub.app.feature.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())

    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {

            is RegisterEvent.NameChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        errorMessage = null
                    )
                }
            }

            is RegisterEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.email,
                        errorMessage = null
                    )
                }
            }

            is RegisterEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        errorMessage = null
                    )
                }
            }

            is RegisterEvent.TermsChanged -> {
                _state.update {
                    it.copy(
                        termsAccepted = event.accepted,
                        errorMessage = null
                    )
                }
            }

            RegisterEvent.TogglePasswordVisibility -> {
                _state.update {
                    it.copy(
                        passwordVisible = !it.passwordVisible
                    )
                }
            }

            RegisterEvent.ContinueClicked -> {
                register()
            }
        }
    }

    private fun register() {
        val currentState = _state.value

        if (currentState.isLoading ||
            currentState.isRegistrationSuccessful
        ) return

        if (currentState.name.isBlank() ||
            currentState.email.isBlank() ||
            currentState.password.isBlank()
        ) {
            _state.update {
                it.copy(
                    errorMessage = "Completa todos los campos"
                )
            }
            return
        }

        if (currentState.name.trim().length < 3) {
            _state.update {
                it.copy(
                    errorMessage = "Ingresa tu nombre completo"
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

        val password = currentState.password

        val validPassword =
            password.length >= 8 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isDigit() }

        if (!validPassword) {
            _state.update {
                it.copy(
                    errorMessage =
                        "La contraseña debe tener 8 caracteres, una mayúscula y un número"
                )
            }
            return
        }

        if (!currentState.termsAccepted) {
            _state.update {
                it.copy(
                    errorMessage = "Debes aceptar los términos y condiciones"
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

            // Registro simulado
            delay(1000)

            _state.update {
                it.copy(
                    isLoading = false,
                    isRegistrationSuccessful = true
                )
            }
        }
    }
}