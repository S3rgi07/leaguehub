package com.leaguehub.app.feature.matches.detail

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.fake.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchDetailViewModel(matchId: String) : ViewModel() {
    private val match = (fakePlayerMatches + fakeRecentResults + fakeMatches)
        .firstOrNull { it.id == matchId } ?: upcomingFalconsVsTitans
    private val _state = MutableStateFlow(MatchDetailState(match))
    val state = _state.asStateFlow()

    fun onEvent(event: MatchDetailEvent) {
        when (event) {
            MatchDetailEvent.RefreshClicked -> Unit
        }
    }
}
