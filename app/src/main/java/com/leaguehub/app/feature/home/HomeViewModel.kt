package com.leaguehub.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.CommunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val communityRepository: CommunityRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
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
