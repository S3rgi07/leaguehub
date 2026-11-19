package com.leaguehub.app.feature.auth.recovery
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable fun RecoveryRoute(onBack:()->Unit) {
 val vm:RecoveryViewModel=viewModel()
 val state by vm.state.collectAsStateWithLifecycle()
 LaunchedEffect(vm) { vm.back.collect { onBack() } }
 RecoveryScreen(state,vm::onEvent)
}
