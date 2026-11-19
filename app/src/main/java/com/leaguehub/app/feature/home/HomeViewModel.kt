package com.leaguehub.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.leaguehub.app.feature.profile.toPlayer

class HomeViewModel(
    private val communityRepository: CommunityRepository,
    private val sessionRepository: com.leaguehub.app.data.repository.UserSessionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState(player = sessionRepository.session.value.toPlayer()))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionRepository.session.collect { session -> _state.update { it.copy(player = session.toPlayer()) } }
        }
        loadAnnouncement()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.RefreshClicked -> loadAnnouncement()
            HomeEvent.RetryAnnouncementClicked -> loadAnnouncement()
        }
    }

    private fun loadAnnouncement() {
        if (_state.value.isAnnouncementLoading) return
        viewModelScope.launch {
            _state.update {
                it.copy(isAnnouncementLoading = true, announcementError = null)
            }
            communityRepository.loadAnnouncement()
                .onSuccess { announcement ->
                    _state.update {
                        it.copy(
                            communityAnnouncement = announcement,
                            isAnnouncementLoading = false
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            isAnnouncementLoading = false,
                            announcementError = "No se pudo actualizar la comunidad."
                        )
                    }
                }
        }
    }
}
