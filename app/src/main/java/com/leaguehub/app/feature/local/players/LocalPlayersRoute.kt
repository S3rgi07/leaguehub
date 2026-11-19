package com.leaguehub.app.feature.local.players
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
@Composable fun LocalPlayersRoute(nav:NavHostController,deps:LocalDependencies,entry:NavBackStackEntry) {
 if(!LocalReady(nav,deps)) return
 val store=localStore(nav,deps)
 val vm:LocalPlayersViewModel=viewModel(factory=viewModelFactory { initializer { LocalPlayersViewModel(store,entry.toRoute<LocalPlayersDestination>().clubId,entry.savedStateHandle) } })
 val state by vm.state.collectAsStateWithLifecycle()
 LocalEffects(vm,nav)
 LocalPlayersScreen(state,vm::onEvent)
}
