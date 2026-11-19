package com.leaguehub.app.feature.local.clubs
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class ClubsViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    val state=store.state.map { s -> ClubsState(s.ui(),s.clubs.map { ClubRow(it.id,it.leagueId,it.name,s.players.count { p->p.clubId==it.id },it.id in s.followedClubIds) },s.canManage) }.stateIn(viewModelScope,SharingStarted.Eagerly,ClubsState())
    fun onEvent(event:ClubsEvent) { when(event) {
        is ClubsEvent.Common -> common(event.event)
        ClubsEvent.Add -> store.onEvent(LocalEvent.Edit("club"))
            is ClubsEvent.Edit -> store.onEvent(LocalEvent.Edit("club",event.id))
            is ClubsEvent.Delete -> { val item=store.state.value.clubs.find { it.id==event.id } ?: return;store.onEvent(LocalEvent.RequestDelete("club",item.id,item.name)) }
            is ClubsEvent.Follow -> store.onEvent(LocalEvent.Follow(event.id))
            is ClubsEvent.Players -> effect(LocalEffect.ClubPlayers(event.id))
    } }
}
