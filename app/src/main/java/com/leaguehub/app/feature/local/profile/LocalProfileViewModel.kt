package com.leaguehub.app.feature.local.profile
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class LocalProfileViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    val state=store.state.map { s -> LocalProfileState(s.ui(),s.myPlayer?.let { s.playerRow(it) },s.data.clubs.filter { it.id in s.followedClubIds }.map { ClubRow(it.id,it.leagueId,it.name,0,true) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,LocalProfileState())
    fun onEvent(event:LocalProfileEvent) { when(event) {
        is LocalProfileEvent.Common -> common(event.event)
        LocalProfileEvent.EditName -> store.onEvent(LocalEvent.Edit("profile"))
            LocalProfileEvent.Password -> store.onEvent(LocalEvent.Edit("password"))
            is LocalProfileEvent.OpenClub -> effect(LocalEffect.ClubPlayers(event.id))
    } }
}
