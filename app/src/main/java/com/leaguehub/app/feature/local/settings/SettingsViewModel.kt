package com.leaguehub.app.feature.local.settings
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class SettingsViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
 private val privacy=MutableStateFlow(false)
 val state=combine(store.state,privacy) { s,p->SettingsState(s.ui(),s.league?.name ?: "Sin liga",p) }.stateIn(viewModelScope,SharingStarted.Eagerly,SettingsState())
 fun onEvent(e:SettingsEvent) { when(e) {
  is SettingsEvent.Common->common(e.event)
  SettingsEvent.EditName->store.onEvent(LocalEvent.Edit("profile"))
  SettingsEvent.Password->store.onEvent(LocalEvent.Edit("password"))
  is SettingsEvent.Privacy->privacy.value=e.visible
 } }
}
