package com.leaguehub.app.feature.local.explore
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class ExploreViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
 private val query=MutableStateFlow("")
 val state=combine(store.state,query) { s,q->ExploreState(s.ui(),q,s.data.leagues.filter { it.name.contains(q,true) || it.season.contains(q,true) }.map { LeagueRow(it.id,it.name,it.season,it.id==s.leagueId,s.canManage) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,ExploreState())
 fun onEvent(e:ExploreEvent) { when(e) { is ExploreEvent.Common->common(e.event);is ExploreEvent.Query->query.value=e.value;is ExploreEvent.Open->effect(LocalEffect.League(e.id)) } }
}
