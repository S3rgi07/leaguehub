package com.leaguehub.app.feature.auth.recovery
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
@Composable fun RecoveryRoute(onBack:()->Unit) {
 val app=LocalContext.current.applicationContext as com.leaguehub.app.LeagueHubApplication
 val vm:RecoveryViewModel=viewModel(factory=viewModelFactory { initializer { RecoveryViewModel(app.authRepository) } })
 val state by vm.state.collectAsStateWithLifecycle()
 LaunchedEffect(vm) { vm.back.collect { onBack() } }
 RecoveryScreen(state,vm::onEvent)
}
