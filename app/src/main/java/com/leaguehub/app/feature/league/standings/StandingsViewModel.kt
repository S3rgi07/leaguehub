package com.leaguehub.app.feature.league.standings

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.fake.fakeAwayStandings
import com.leaguehub.app.data.fake.fakeHomeStandings
import com.leaguehub.app.data.fake.fakeStandings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StandingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(StandingsState())
    val state: StateFlow<StandingsState> = _state.asStateFlow()

    fun onEvent(event: StandingsEvent) {
        when (event) {
            is StandingsEvent.ScopeChanged -> _state.update {
                it.copy(
                    scope = event.scope,
                    standings = when (event.scope) {
                        StandingsScope.GENERAL -> fakeStandings
                        StandingsScope.HOME -> fakeHomeStandings
                        StandingsScope.AWAY -> fakeAwayStandings
                    }
                )
            }
        }
    }
}
