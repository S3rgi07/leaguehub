package com.leaguehub.app.feature.auth.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
    val target: LoginTarget? = null,
    val navigationAction: LoginAction = LoginAction.NONE
)
enum class LoginTarget { ROLE, LOCAL }

enum class LoginAction { NONE, RECOVERY, REGISTER }
