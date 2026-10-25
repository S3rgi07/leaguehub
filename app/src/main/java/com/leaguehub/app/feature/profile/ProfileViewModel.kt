package com.leaguehub.app.feature.profile

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.fake.fakePlayers
import com.leaguehub.app.data.fake.fakeTeams
import com.leaguehub.app.data.fake.sofia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(playerId: String) : ViewModel() {
    private val player = fakePlayers.firstOrNull { it.id == playerId } ?: sofia
    private val _state = MutableStateFlow(
        ProfileState(player, fakeTeams.first { it.id == player.teamId })
    )
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.RefreshClicked -> Unit
        }
    }
}
