package com.leaguehub.app.feature.local.standings
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class StandingsViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    val state=store.state.map { s -> StandingsState(s.ui(),s.leagueId?.let { s.data.table(it) }.orEmpty().mapIndexed { index,row -> PositionRow(row.club.id,"${index+1}. ${row.club.name}","${row.played} · ${row.won} · ${row.drawn} · ${row.lost} · ${row.goalsFor}:${row.goalsAgainst} · ${row.difference} · ${row.points} PTS") }) }.stateIn(viewModelScope,SharingStarted.Eagerly,StandingsState())
    fun onEvent(event:StandingsEvent) { when(event) {
        is StandingsEvent.Common -> common(event.event)
        
    } }
}
