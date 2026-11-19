package com.leaguehub.app.feature.local.players
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class LocalPlayersViewModel(store:LocalHubViewModel,clubId:String?=null,private val saved:SavedStateHandle=SavedStateHandle()):LocalFeatureViewModel(store) {
 private val filter=saved.getStateFlow("clubFilter",clubId.orEmpty())
 private fun scoped(s:LocalState,clubId:String):LocalState {
  val league=s.data.clubs.find { it.id==clubId }?.leagueId ?: s.leagueId
  return s.copy(leagueId=league,clubId=clubId)
 }
 val state=combine(store.state,filter) { original,selected ->
  val s=scoped(original,selected)
  LocalPlayersState(s.ui(),s.players.filter { selected.isEmpty() || it.clubId==selected }.map { s.playerRow(it) },listOf("" to "Todos los equipos")+s.clubs.map { it.id to it.name },selected,s.canManage,s.canManage && s.clubs.isNotEmpty())
 }.stateIn(viewModelScope,SharingStarted.Eagerly,LocalPlayersState())
 private fun activate() { val s=scoped(store.state.value,filter.value);if(s.leagueId!=store.state.value.leagueId) s.leagueId?.let { store.onEvent(LocalEvent.SelectLeague(it)) };store.onEvent(LocalEvent.SelectClub(filter.value.takeIf { it.isNotBlank() })) }
 fun onEvent(event:LocalPlayersEvent) { when(event) {
  is LocalPlayersEvent.Common -> { if(event.event is LocalEvent.SelectLeague) saved["clubFilter"]="";common(event.event) }
  is LocalPlayersEvent.Filter -> { saved["clubFilter"]=event.clubId;activate() }
  LocalPlayersEvent.Add -> { activate();store.onEvent(LocalEvent.Edit("player")) }
  is LocalPlayersEvent.Edit -> { activate();store.onEvent(LocalEvent.Edit("player",event.id)) }
  is LocalPlayersEvent.Delete -> { activate();val player=store.state.value.players.find { it.id==event.id } ?: return;store.onEvent(LocalEvent.RequestDelete("player",player.id,player.name)) }
 } }
}
