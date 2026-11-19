package com.leaguehub.app.feature.local.events
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class LocalEventsViewModel(store:LocalHubViewModel,private val matchId:String):LocalFeatureViewModel(store) {
 private fun scoped(s:LocalState):LocalState = s.copy(leagueId=s.data.fixtures.find { it.id==matchId }?.leagueId,fixtureId=matchId)
 val state=store.state.map { original ->
  val s=scoped(original);val match=s.fixture
  LocalEventsState(s.ui(),match?.let { s.matchRow(it) },s.data.events.filter { it.fixtureId==matchId }.map { e -> EventRow(e.id,"${e.minute}′ · ${eventTitle(e.kind)}",s.data.players.find { it.id==e.playerId }?.name.orEmpty(),if(e.kind=="APPEARANCE") "${e.minutesPlayed} minutos · valoración ${e.rating ?: "—"}" else null) },s.canManage,s.canManage && match!=null && s.players.any { it.clubId==match.homeId || it.clubId==match.awayId })
 }.stateIn(viewModelScope,SharingStarted.Eagerly,LocalEventsState())
 private fun activate():Boolean { val fixture=store.state.value.data.fixtures.find { it.id==matchId } ?: return false;if(store.state.value.leagueId!=fixture.leagueId) store.onEvent(LocalEvent.SelectLeague(fixture.leagueId));store.onEvent(LocalEvent.SelectFixture(matchId));return true }
 fun onEvent(event:LocalEventsEvent) { when(event) {
  is LocalEventsEvent.Common -> common(event.event)
  LocalEventsEvent.Add -> if(activate()) store.onEvent(LocalEvent.Edit("event"))
  is LocalEventsEvent.Edit -> if(activate() && store.state.value.data.events.any { it.id==event.id && it.fixtureId==matchId }) store.onEvent(LocalEvent.Edit("event",event.id))
  is LocalEventsEvent.Delete -> { if(!activate()) return;val item=store.state.value.data.events.find { it.id==event.id && it.fixtureId==matchId } ?: return;store.onEvent(LocalEvent.RequestDelete("event",item.id,eventTitle(item.kind))) }
  is LocalEventsEvent.Action -> if(activate()) { if(event.action==MatchAction.EVENTS) return;matchAction(matchId,event.action) }
 } }
}
