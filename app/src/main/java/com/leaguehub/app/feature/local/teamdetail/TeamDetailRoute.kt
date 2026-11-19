package com.leaguehub.app.feature.local.teamdetail
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.navigation.*
@Composable fun TeamDetailRoute(nav:NavHostController,deps:LocalDependencies,entry:NavBackStackEntry) {
 if(!LocalReady(nav,deps)) return
 val store=localStore(nav,deps,entry)
 val vm:TeamDetailViewModel=viewModel(factory=viewModelFactory { initializer { TeamDetailViewModel(store,entry.toRoute<TeamDetailDestination>().teamId) } })
 val state by vm.state.collectAsStateWithLifecycle()
 LocalEffects(vm,nav)
 TeamDetailScreen(state,vm::onEvent)
}
