package com.leaguehub.app.feature.local.center
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
@Composable fun CenterRoute(nav:NavHostController,deps:LocalDependencies,entry:NavBackStackEntry) {
 if(!LocalReady(nav,deps)) return
 val store=localStore(nav,deps,entry)
 val vm:CenterViewModel=viewModel(factory=viewModelFactory { initializer { CenterViewModel(store,entry.toRoute<MatchCenterDestination>().matchId,deps.sheets, deps.resolver, entry.savedStateHandle) } })
 val state by vm.state.collectAsStateWithLifecycle()
 LocalEffects(vm,nav,onImageResult={vm.onEvent(CenterEvent.PhotoPicked(it))})
 CenterScreen(state,vm::onEvent)
}
