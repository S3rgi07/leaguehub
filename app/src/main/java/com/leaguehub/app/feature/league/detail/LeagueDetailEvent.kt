package com.leaguehub.app.feature.league.detail

sealed interface LeagueDetailEvent {
    data class TabChanged(val tab: LeagueTab) : LeagueDetailEvent
    data class NewsSelected(val newsId: String) : LeagueDetailEvent
    data object NewsDismissed : LeagueDetailEvent
}
