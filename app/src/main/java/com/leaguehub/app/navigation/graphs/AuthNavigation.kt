package com.leaguehub.app.navigation.graphs

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.leaguehub.app.feature.auth.login.LoginScreen
import com.leaguehub.app.feature.auth.recovery.RecoveryScreen
import com.leaguehub.app.feature.auth.register.RegisterScreen
import com.leaguehub.app.feature.onboarding.splash.SplashScreen
import com.leaguehub.app.feature.onboarding.welcome.OnboardingScreen
import com.leaguehub.app.navigation.*
import kotlinx.coroutines.delay
import com.leaguehub.app.feature.onboarding.role.RoleSelectionScreen
import com.leaguehub.app.feature.onboarding.role.UserRole
import com.leaguehub.app.feature.onboarding.league.LeagueSelectionScreen
import com.leaguehub.app.feature.onboarding.permissions.PermissionsScreen
import com.leaguehub.app.feature.auth.login.LoginRoute
fun NavGraphBuilder.authNavigation(
    navController: NavHostController
) {
    composable<SplashDestination> {
        LaunchedEffect(Unit) {
            delay(2000)

            navController.navigate(OnboardingDestination) {
                popUpTo<SplashDestination> {
                    inclusive = true
                }
            }
        }

        SplashScreen()
    }

    composable<OnboardingDestination> {
        OnboardingScreen(
            onStartClick = {
                navController.navigate(RegisterDestination)
            },
            onLoginClick = {
                navController.navigate(LoginDestination)
            }
        )
    }

    composable<LoginDestination> {
        LoginRoute(
            onLoginSuccess = {
                // Se conectará a Home cuando exista en el NavHost
            },
            onForgotPasswordClick = {
                navController.navigate(RecoveryDestination)
            },
            onCreateAccountClick = {
                navController.navigate(RegisterDestination)
            }
        )
    }

    composable<RegisterDestination> {
        RegisterScreen(
            name = "",
            email = "",
            password = "",
            onContinueClick = {
                navController.navigate(RoleSelectionDestination)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
    composable<RoleSelectionDestination> {
        RoleSelectionScreen(
            selectedRole = UserRole.PLAYER,
            onContinueClick = {
                navController.navigate(LeagueSelectionDestination)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<LeagueSelectionDestination> {
        LeagueSelectionScreen(
            leagues = emptyList(),
            popularLeagues = emptyList(),
            selectedLeagueId = null,
            onContinueClick = {
                navController.navigate(PermissionsDestination)
            },
            onExploreLaterClick = {
                navController.navigate(PermissionsDestination)
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable<PermissionsDestination> {
        PermissionsScreen(
            liveMatchesEnabled = true,
            remindersEnabled = true,
            smartPreviewEnabled = true,
            locationEnabled = false,
        )
    }

    composable<RecoveryDestination> {
        RecoveryScreen(
            email = "",
            onBackClick = {
                navController.popBackStack()
            },
            onLoginClick = {
                navController.popBackStack()
            }
        )
    }
}
