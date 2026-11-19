package com.leaguehub.app.feature.local.leagues
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class LeaguesViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    val state=store.state.map { s -> LeaguesState(s.ui(),s.data.leagues.map { LeagueRow(it.id,it.name,it.season,it.id==s.leagueId,s.session.role==com.leaguehub.app.data.session.SessionRole.ORGANIZER && it.ownerId==s.session.userId) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,LeaguesState())
    fun onEvent(event:LeaguesEvent) { when(event) {
        is LeaguesEvent.Details -> effect(LocalEffect.League(event.id))
        is LeaguesEvent.Common -> common(event.event)
        is LeaguesEvent.Open -> { store.onEvent(LocalEvent.SelectLeague(event.id));effect(LocalEffect.Section(LocalSection.CLUBS)) }
            is LeaguesEvent.Edit -> { event.id?.let { store.onEvent(LocalEvent.SelectLeague(it)) };store.onEvent(LocalEvent.Edit("league",event.id)) }
            is LeaguesEvent.Delete -> { val item=store.state.value.data.leagues.find { it.id==event.id } ?: return;store.onEvent(LocalEvent.SelectLeague(item.id));store.onEvent(LocalEvent.RequestDelete("league",item.id,item.name)) }
    } }
}
