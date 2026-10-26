package com.leaguehub.app.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.leaguehub.app.navigation.*
import com.leaguehub.app.feature.auth.login.LoginRoute
import com.leaguehub.app.feature.auth.register.RegisterRoute
import com.leaguehub.app.feature.onboarding.role.RoleSelectionRoute
import com.leaguehub.app.feature.onboarding.league.LeagueSelectionRoute
import com.leaguehub.app.feature.onboarding.permissions.PermissionsRoute
import com.leaguehub.app.feature.auth.recovery.RecoveryRoute
import com.leaguehub.app.feature.onboarding.splash.SplashRoute
import com.leaguehub.app.feature.onboarding.welcome.OnboardingRoute
import androidx.compose.ui.platform.LocalContext
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.data.session.SessionRole

fun NavGraphBuilder.authNavigation(
    navController: NavHostController
) {
    composable<SplashDestination> {
        SplashRoute(
            onFinished = {
                navController.navigate(OnboardingDestination) {
                    popUpTo<SplashDestination> {
                        inclusive = true
                    }
                }
            }
        )
    }

    composable<OnboardingDestination> {
        OnboardingRoute(
            onStart = {
                navController.navigate(RegisterDestination)
            },
            onLogin = {
                navController.navigate(LoginDestination)
            }
        )
    }

    composable<LoginDestination> {

        val application = LocalContext.current.applicationContext
                as LeagueHubApplication

        val sessionRepository = application.userSessionRepository

        LoginRoute(
            onLoginSuccess = {
                val session = sessionRepository.session.value

                when {
                    session.onboardingCompleted &&
                            session.role == SessionRole.PLAYER -> {

                        navController.navigate(HomeDestination) {
                            popUpTo<OnboardingDestination> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    session.onboardingCompleted &&
                            session.role == SessionRole.ORGANIZER -> {

                        navController.navigate(OrganizerDashboardDestination) {
                            popUpTo<OnboardingDestination> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    else -> {
                        navController.navigate(RoleSelectionDestination) {
                            popUpTo<LoginDestination> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
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
                android.util.Log.d(
                    "LeagueHubSession",
                    "Rol seleccionado: $selectedRole"
                )

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

        val application = LocalContext.current.applicationContext
                as LeagueHubApplication

        val sessionRepository = application.userSessionRepository

        PermissionsRoute(
            onFinish = {
                when (sessionRepository.session.value.role) {

                    SessionRole.PLAYER -> {
                        navController.navigate(HomeDestination) {
                            popUpTo<OnboardingDestination> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    SessionRole.ORGANIZER -> {
                        navController.navigate(OrganizerDashboardDestination) {
                            popUpTo<OnboardingDestination> {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    null -> {
                        navController.navigate(RoleSelectionDestination)
                    }
                }
            }
        )
    }

    composable<RecoveryDestination> {
        RecoveryRoute(
            onBack = {
                navController.popBackStack()
            },
            onLogin = {
                navController.popBackStack()
            }
        )
    }
}
