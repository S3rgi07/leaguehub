package com.leaguehub.app.feature.local.home
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.data.repository.CommunityRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class LocalHomeViewModel(store:LocalHubViewModel,private val community:CommunityRepository):LocalFeatureViewModel(store) {
 private val remote=MutableStateFlow(RemoteAnnouncementState())
 val state=combine(store.state,remote) { s,announcement -> LocalHomeState(s.ui(),s.league?.name,"${s.clubs.size} equipos · ${s.players.size} jugadores · ${s.fixtures.size} partidos","${s.fixtures.count { it.homeScore!=null }} resultados registrados",s.fixtures.filter { it.homeScore==null }.sortedBy { it.kickoff }.take(3).map { s.matchRow(it) },s.canManage,announcement,s.fixtures.filter { it.homeScore!=null }.sortedByDescending { it.kickoff }.take(5).map { s.matchRow(it) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,LocalHomeState())
 init { loadAnnouncement() }
 fun onEvent(event:LocalHomeEvent) { when(event) {
  is LocalHomeEvent.Common -> common(event.event)
  is LocalHomeEvent.MatchActionSelected -> matchAction(event.id,event.action)
  LocalHomeEvent.RetryAnnouncement -> loadAnnouncement()
 } }
 private fun loadAnnouncement() {
  if(remote.value.loading) return
  remote.value=RemoteAnnouncementState(loading=true)
  viewModelScope.launch {
   community.loadAnnouncement().onSuccess { remote.value=RemoteAnnouncementState(text=it) }
    .onFailure { remote.value=RemoteAnnouncementState(error="No se pudo cargar el anuncio remoto. Tus datos locales siguen disponibles.") }
  }
 }
}
