package com.leaguehub.app.feature.local
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavBackStackEntry
import com.leaguehub.app.navigation.*

@Composable fun localStore(nav:NavHostController,deps:LocalDependencies,entry:NavBackStackEntry):LocalHubViewModel {
 val parent=remember(nav,entry) { nav.getBackStackEntry<LocalHubDestination>() }
 return viewModel(viewModelStoreOwner=parent,key="local-store",factory=viewModelFactory { initializer { deps.createStore() } })
}
@Composable fun LocalReady(nav:NavHostController,deps:LocalDependencies):Boolean {
 val restored by deps.sessions.restoreCompleted.collectAsStateWithLifecycle()
 val session by deps.sessions.session.collectAsStateWithLifecycle()
 LaunchedEffect(restored,session.isAuthenticated) {
  if(restored && !session.isAuthenticated) nav.navigate(LoginDestination) { popUpTo<LocalHubDestination> { inclusive=true };launchSingleTop=true }
 }
 if(!restored || !session.isAuthenticated) { Box(Modifier.fillMaxSize(),contentAlignment=androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() };return false }
 return true
}
@Composable fun LocalEffects(vm:LocalFeatureViewModel,nav:NavHostController,onImageResult:(Uri?)->Unit={},onFileResult:(Boolean,Uri?)->Unit={_,_->}) {
 val context=LocalContext.current
 val lifecycle=LocalLifecycleOwner.current
 val latestImageResult by rememberUpdatedState(onImageResult)
 val imagePicker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { latestImageResult(it) }
 val latestFileResult by rememberUpdatedState(onFileResult)
 val exporter=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { latestFileResult(true,it) }
 val importer=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { latestFileResult(false,it) }
 LaunchedEffect(vm,nav,lifecycle) { lifecycle.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
  vm.effects.collect { effect -> when(effect) {
   is LocalEffect.Page -> nav.navigate(when(effect.page) { LocalPage.CLOUD->CloudSyncDestination;LocalPage.SETTINGS->SettingsDestination;LocalPage.ALERTS->NotificationsDestination;LocalPage.EXPLORE->ExploreDestination;LocalPage.DASHBOARD->OrganizerDashboardDestination }) { launchSingleTop=true }
   is LocalEffect.League -> nav.navigate(LeagueDetailDestination(effect.id)) { launchSingleTop=true }
   is LocalEffect.Team -> nav.navigate(TeamDetailDestination(effect.id)) { launchSingleTop=true }
   is LocalEffect.Center -> nav.navigate(MatchCenterDestination(effect.id)) { launchSingleTop=true }
   is LocalEffect.Details -> nav.navigate(MatchDetailDestination(effect.id)) { launchSingleTop=true }
   LocalEffect.ImageFile -> imagePicker.launch(arrayOf("image/*"))
   is LocalEffect.Section -> {
    val target:Any=when(effect.section) {
     LocalSection.HOME->LocalHomeDestination
     LocalSection.LEAGUES->LocalLeaguesDestination
     LocalSection.CLUBS->LocalClubsDestination
     LocalSection.PLAYERS->LocalPlayersDestination()
     LocalSection.MATCHES->LocalMatchesDestination
     LocalSection.TABLE->LocalStandingsDestination
     LocalSection.PROFILE->LocalProfileDestination
    }
    nav.navigate(target) { popUpTo<LocalHomeDestination>();launchSingleTop=true }
   }
   is LocalEffect.ClubPlayers -> nav.navigate(LocalPlayersDestination(effect.clubId)) { launchSingleTop=true }
   is LocalEffect.MatchEvents -> nav.navigate(LocalEventsDestination(effect.matchId)) { launchSingleTop=true }
   LocalEffect.Back -> nav.popBackStack()
   LocalEffect.Backups -> nav.navigate(LocalBackupDestination) { launchSingleTop=true }
   is LocalEffect.ExportFile -> exporter.launch(effect.fileName)
   LocalEffect.ImportFile -> importer.launch(arrayOf("application/json","text/plain","application/octet-stream"))
   is LocalEffect.Location -> try { context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="+Uri.encode(effect.query)))) } catch(_:android.content.ActivityNotFoundException) { Toast.makeText(context,"No hay una aplicación de mapas instalada.",Toast.LENGTH_LONG).show() }
   is LocalEffect.Share -> context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,effect.text),"Compartir partido"))
  } }
 } }
}
