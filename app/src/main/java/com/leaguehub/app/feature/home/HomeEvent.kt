package com.leaguehub.app.feature.home

sealed interface HomeEvent {
    data object RefreshClicked : HomeEvent
}
