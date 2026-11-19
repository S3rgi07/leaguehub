package com.leaguehub.app.feature.local.standings
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class StandingsViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    private val filter=MutableStateFlow(0)
    val state=combine(store.state,filter) { s,scope ->
        val table=s.leagueId?.let { leagueId ->
            val general=s.data.table(leagueId)
            if(scope==0) general else general.map { row ->
                val fixtures=s.data.fixtures.filter { if(scope==1) it.homeId==row.club.id else it.awayId==row.club.id }
                s.data.copy(fixtures=fixtures).table(leagueId).first { it.club.id==row.club.id }
            }.sortedWith(compareByDescending<com.leaguehub.app.data.repository.TableRow> { it.points }.thenByDescending { it.difference }.thenByDescending { it.goalsFor }.thenBy { it.club.name })
        }.orEmpty()
        StandingsState(s.ui(),table.mapIndexed { index,row -> PositionRow(row.club.id,"${index+1}. ${row.club.name}","${row.played} · ${row.won} · ${row.drawn} · ${row.lost} · ${row.goalsFor}:${row.goalsAgainst} · ${row.difference} · ${row.points} PTS",row.club.name,index+1,row.played,row.difference,row.points) },scope)
    }.stateIn(viewModelScope,SharingStarted.Eagerly,StandingsState())
    fun onEvent(event:StandingsEvent) { when(event) {
        is StandingsEvent.Filter -> if(event.index in 0..2) filter.value=event.index
        is StandingsEvent.Common -> common(event.event)
        
    } }
}
