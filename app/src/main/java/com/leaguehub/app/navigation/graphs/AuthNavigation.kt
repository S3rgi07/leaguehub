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
        val app=LocalContext.current.applicationContext as LeagueHubApplication
        val scope=rememberCoroutineScope()
        SplashRoute(onFinished={ scope.launch {
            val session=app.userSessionRepository.session.value
            if (session.isAuthenticated && session.userId?.let { app.database.userAccountDao().findById(it) } == null) {
                app.userSessionRepository.logoutAndWait()
                android.widget.Toast.makeText(app,"La sesión anterior era de demostración. Crea una cuenta local para continuar.",android.widget.Toast.LENGTH_LONG).show()
                open(OnboardingDestination)
            } else open(when { !session.isAuthenticated -> OnboardingDestination; session.role==null -> RoleSelectionDestination; else -> LocalHubDestination })
        } })
    }
    composable<OnboardingDestination> {
        OnboardingRoute(onStart={navController.navigate(RegisterDestination)},onLogin={navController.navigate(LoginDestination)})
    }
    composable<LoginDestination> {
        val app=LocalContext.current.applicationContext as LeagueHubApplication
        LoginRoute(onLoginSuccess={open(if(app.userSessionRepository.session.value.role==null) RoleSelectionDestination else LocalHubDestination)},
            onForgotPasswordClick={navController.navigate(RecoveryDestination)},onCreateAccountClick={navController.navigate(RegisterDestination)})
    }
    composable<RegisterDestination> {
        RegisterRoute(onContinue={open(RoleSelectionDestination)},onBack={navController.popBackStack()})
    }
    composable<RoleSelectionDestination> {
        val app=LocalContext.current.applicationContext as LeagueHubApplication
        RoleSelectionRoute(onContinue={app.userSessionRepository.completeOnboarding();open(LocalHubDestination)},onBack={app.userSessionRepository.logout();open(LoginDestination)},
            onConfigureLeague={app.userSessionRepository.selectRole(SessionRole.ORGANIZER);app.userSessionRepository.completeOnboarding();open(LocalHubDestination)})
    }
    composable<RecoveryDestination> {
        Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
            Text("Recuperar acceso",style=MaterialTheme.typography.headlineMedium)
            Text("Las cuentas se guardan únicamente en este teléfono. Esta versión local no envía correos ni códigos de recuperación.")
            Text("Si todavía tienes la sesión abierta, puedes cambiar la contraseña desde Perfil usando la contraseña actual. Si la olvidaste, crea otra cuenta; los registros de las ligas permanecen en este teléfono, pero la nueva cuenta no tendrá los permisos de la anterior.")
            Button(onClick={navController.popBackStack()}) { Text("Volver al acceso") }
        }
    }
}
