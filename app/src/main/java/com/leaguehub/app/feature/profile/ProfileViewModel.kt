package com.leaguehub.app.feature.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.fake.fakePlayers
import com.leaguehub.app.data.fake.fakeTeams
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.data.session.UserSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(private val playerId: String?, sessionRepository: UserSessionRepository) : ViewModel() {
    private fun profile(session: UserSession): ProfileState {
        val player = if (playerId == null || playerId == session.userId) session.toPlayer()
            else fakePlayers.firstOrNull { it.id == playerId }
                ?: UserSession(userName = "Jugador no encontrado").toPlayer()
        return ProfileState(player, fakeTeams.firstOrNull { it.id == player.teamId })
    }
    val state = sessionRepository.session.map(::profile).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), profile(sessionRepository.session.value)
    )
    fun onEvent(event: ProfileEvent) = Unit
}