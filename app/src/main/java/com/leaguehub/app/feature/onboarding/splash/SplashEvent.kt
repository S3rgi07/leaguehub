package com.leaguehub.app.feature.onboarding.splash

sealed interface SplashEvent {
    data object Initialize : SplashEvent
}