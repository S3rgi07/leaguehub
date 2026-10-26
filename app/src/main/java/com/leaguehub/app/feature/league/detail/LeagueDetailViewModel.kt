package com.leaguehub.app.feature.league.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LeagueDetailViewModel(private val leagueId: String) : ViewModel() {
    private val _state = MutableStateFlow(LeagueDetailState())
    val state: StateFlow<LeagueDetailState> = _state.asStateFlow()

    fun onEvent(event: LeagueDetailEvent) {
        when (event) {
            is LeagueDetailEvent.TabChanged -> _state.update { it.copy(selectedTab = event.tab) }
            is LeagueDetailEvent.NewsSelected -> _state.update { current ->
                current.copy(selectedNews = current.news.firstOrNull { it.id == event.newsId })
            }
            LeagueDetailEvent.NewsDismissed -> _state.update { it.copy(selectedNews = null) }
        }
    }
}
