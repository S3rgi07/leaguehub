package com.leaguehub.app.feature.onboarding.welcome

sealed interface OnboardingEvent {

    data object StartClicked : OnboardingEvent

    data object LoginClicked : OnboardingEvent

    data object NavigationHandled : OnboardingEvent
}