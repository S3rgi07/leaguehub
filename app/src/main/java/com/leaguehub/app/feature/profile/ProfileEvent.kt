package com.leaguehub.app.feature.profile

sealed interface ProfileEvent {
    data object RefreshClicked : ProfileEvent
}
