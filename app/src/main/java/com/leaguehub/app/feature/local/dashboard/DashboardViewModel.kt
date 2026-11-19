package com.leaguehub.app.feature.local.dashboard
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class DashboardViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
 val state=store.state.map { s->DashboardState(s.ui(),s.league?.name ?: "Sin liga seleccionada",s.clubs.size,s.players.size,s.fixtures.count { it.homeScore!=null },s.fixtures.size,s.fixtures.filter { it.homeScore==null }.sortedBy { it.kickoff }.map { s.matchRow(it) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,DashboardState())
 fun onEvent(e:DashboardEvent) { when(e) { is DashboardEvent.Common->common(e.event);is DashboardEvent.Match->effect(LocalEffect.Center(e.id));DashboardEvent.Create->common(LocalEvent.Navigate(LocalSection.MATCHES)) } }
}
