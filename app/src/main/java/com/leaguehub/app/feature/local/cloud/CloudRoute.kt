package com.leaguehub.app.feature.local.cloud
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.feature.local.*
@Composable fun CloudRoute(nav:NavHostController,deps:LocalDependencies,entry:NavBackStackEntry) {
 if(!LocalReady(nav,deps)) return
 val store=localStore(nav,deps,entry)
 val app=LocalContext.current.applicationContext as LeagueHubApplication
 val vm:CloudViewModel=viewModel(factory=viewModelFactory { initializer { CloudViewModel(store,app.cloudSync,app.authRepository,deps.resolver) } })
 val state by vm.state.collectAsStateWithLifecycle()
 LocalEffects(vm,nav,onFileResult={_,uri->vm.onEvent(CloudEvent.ExportPicked(uri))})
 CloudScreen(state,vm::onEvent)
}
