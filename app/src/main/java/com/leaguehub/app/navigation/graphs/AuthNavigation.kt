package com.leaguehub.app.navigation.graphs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.feature.auth.login.LoginRoute
import com.leaguehub.app.feature.auth.register.RegisterRoute
import com.leaguehub.app.feature.onboarding.role.RoleSelectionRoute
import com.leaguehub.app.feature.onboarding.splash.SplashRoute
import com.leaguehub.app.feature.onboarding.welcome.OnboardingRoute
import com.leaguehub.app.navigation.*

fun NavGraphBuilder.authNavigation(navController: NavHostController) {
    fun open(destination: Any) {
        navController.navigate(destination) { popUpTo(navController.graph.id); launchSingleTop = true }
    }
    composable<SplashDestination> {
        SplashRoute(onFinished={ target -> open(when(target) {
            com.leaguehub.app.feature.onboarding.splash.SplashTarget.ONBOARDING -> OnboardingDestination
            com.leaguehub.app.feature.onboarding.splash.SplashTarget.ROLE -> RoleSelectionDestination
            com.leaguehub.app.feature.onboarding.splash.SplashTarget.LOCAL -> LocalHubDestination
        }) })
    }
    composable<OnboardingDestination> {
        OnboardingRoute(onStart={navController.navigate(RegisterDestination)},onLogin={navController.navigate(LoginDestination)})
    }
    composable<LoginDestination> {
        LoginRoute(onLoginSuccess={ target -> open(if(target==com.leaguehub.app.feature.auth.login.LoginTarget.ROLE) RoleSelectionDestination else LocalHubDestination) },
            onForgotPasswordClick={navController.navigate(RecoveryDestination)},onCreateAccountClick={navController.navigate(RegisterDestination)})
    }
    composable<RegisterDestination> {
        RegisterRoute(onContinue={open(RoleSelectionDestination)},onBack={navController.popBackStack()})
    }
    composable<RoleSelectionDestination> {
        RoleSelectionRoute(onContinue={open(LocalHubDestination)},onBack={open(LoginDestination)})
    }
    composable<RecoveryDestination> {
        com.leaguehub.app.feature.auth.recovery.RecoveryRoute(onBack={navController.popBackStack()})
    }
}
