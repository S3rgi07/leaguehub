package com.leaguehub.app.feature.onboarding.welcome

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val navigationAction: OnboardingAction = OnboardingAction.NONE
)