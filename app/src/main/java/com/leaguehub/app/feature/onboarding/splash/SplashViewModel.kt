package com.leaguehub.app.feature.onboarding.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.UserSessionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val sessionRepository: UserSessionRepository,
    private val accounts: com.leaguehub.app.data.local.UserAccountDao
) : ViewModel() {

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
            sessionRepository.restoreCompleted.first { it }
            val session=sessionRepository.session.value
            var message:String?=null
            val target=if(!session.isAuthenticated) SplashTarget.ONBOARDING
                else if(session.userId?.let { accounts.findById(it) }==null) {
                    sessionRepository.logoutAndWait()
                    message="La sesión anterior era de demostración. Crea una cuenta local para continuar."
                    SplashTarget.ONBOARDING
                } else if(session.role==null) SplashTarget.ROLE else SplashTarget.LOCAL
            _state.value=SplashState(isLoading=false,isReady=true,target=target,message=message)
        }
    }
}
