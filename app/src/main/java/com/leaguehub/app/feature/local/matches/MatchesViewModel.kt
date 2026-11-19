package com.leaguehub.app.feature.local.matches
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class MatchesViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    val state=store.state.map { s -> MatchesState(s.ui(),s.fixtures.map { s.matchRow(it) },s.canManage,s.canManage && s.clubs.size>=2,s.canManage && s.clubs.size<2) }.stateIn(viewModelScope,SharingStarted.Eagerly,MatchesState())
    fun onEvent(event:MatchesEvent) { when(event) {
        is MatchesEvent.Common -> common(event.event)
        MatchesEvent.Add -> store.onEvent(LocalEvent.Edit("fixture"))
            is MatchesEvent.Action -> matchAction(event.id,event.action)
    } }
}
