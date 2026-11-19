package com.leaguehub.app.feature.notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun notificationsViewModel(): NotificationsViewModel {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    return viewModel(factory = viewModelFactory { initializer { NotificationsViewModel(application.notificationsState) } })
}