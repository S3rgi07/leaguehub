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
import com.leaguehub.app.feature.auth.register.RegisterRoute
import com.leaguehub.app.feature.onboarding.role.RoleSelectionRoute
import com.leaguehub.app.feature.onboarding.league.LeagueSelectionRoute
import com.leaguehub.app.feature.onboarding.permissions.PermissionsRoute

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
        RegisterRoute(
            onContinue = {
                navController.navigate(RoleSelectionDestination) {
                    popUpTo<RegisterDestination> {
                        inclusive = true
                    }
                }
            },
            onBack = {
                navController.popBackStack()
            }
        )
    }

    composable<RoleSelectionDestination> {
        RoleSelectionRoute(
            onContinue = { selectedRole ->

                // Más adelante guardaremos el rol
                // en un repositorio compartido.

                navController.navigate(LeagueSelectionDestination)
            },

            onBack = {
                navController.popBackStack()
            },

            onConfigureLeague = {
                navController.navigate(LeagueSelectionDestination)
            }
        )
    }

    composable<LeagueSelectionDestination> {
        LeagueSelectionRoute(
            onContinue = { selectedLeagueId ->
                // Más adelante guardaremos la liga seleccionada
                // en un repositorio compartido.

                navController.navigate(PermissionsDestination)
            },

            onBack = {
                navController.popBackStack()
            }
        )
    }

    composable<PermissionsDestination> {
        PermissionsRoute(
            onFinish = {
                // Pendiente:
                // Navegar a HomeDestination o
                // OrganizerDashboardDestination
                // según el rol seleccionado.
            }
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
