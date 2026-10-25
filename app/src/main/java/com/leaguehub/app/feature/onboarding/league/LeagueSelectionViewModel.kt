package com.leaguehub.app.feature.onboarding.league

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.leaguehub.app.data.repository.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeagueSelectionViewModel(
    private val sessionRepository: UserSessionRepository
) : ViewModel() {
    private val allLeagues = listOf(
        LeagueOptionUiModel(
            id = "university",
            name = "Liga Universitaria GT",
            description = "12 equipos · Jornada 8",
            abbreviation = "LU",
            color = Color(0xFF76A942)
        ),
        LeagueOptionUiModel(
            id = "zona7",
            name = "Copa Zona 7",
            description = "8 equipos · Apertura",
            abbreviation = "C7",
            color = Color(0xFFE8752E)
        ),
        LeagueOptionUiModel(
            id = "amateur",
            name = "Amateur Metropolitana",
            description = "16 equipos · Clausura",
            abbreviation = "AM",
            color = Color(0xFF1687D4)
        )
    )

    private val allPopularLeagues = listOf(
        LeagueOptionUiModel(
            id = "campus",
            name = "Liga Fútbol Campus",
            description = "10 equipos · Mixta",
            abbreviation = "FC",
            color = Color(0xFF1687D4)
        )
    )

    private val _state = MutableStateFlow(
        LeagueSelectionState(
            leagues = allLeagues,
            popularLeagues = allPopularLeagues,
            selectedLeagueId = sessionRepository.session.value.selectedLeagueId
        )
    )

    val state: StateFlow<LeagueSelectionState> = _state.asStateFlow()

    fun onEvent(event: LeagueSelectionEvent) {
        when (event) {

            is LeagueSelectionEvent.SearchChanged -> {
                updateSearch(event.query)
            }

            is LeagueSelectionEvent.LeagueSelected -> {
                _state.update {
                    it.copy(
                        selectedLeagueId = event.leagueId,
                        errorMessage = null
                    )
                }
            }

            LeagueSelectionEvent.ContinueClicked -> {
                confirmSelection()
            }

            LeagueSelectionEvent.ExploreLaterClicked -> {
                sessionRepository.selectLeague(null)

                _state.update {
                    it.copy(
                        selectedLeagueId = null,
                        isSkipped = true
                    )
                }
            }
        }
    }

    private fun updateSearch(query: String) {
        val filteredLeagues = allLeagues.filter { league ->
            league.name.contains(query, ignoreCase = true) ||
                    league.description.contains(query, ignoreCase = true)
        }

        val filteredPopular = allPopularLeagues.filter { league ->
            league.name.contains(query, ignoreCase = true) ||
                    league.description.contains(query, ignoreCase = true)
        }

        _state.update {
            it.copy(
                searchQuery = query,
                leagues = filteredLeagues,
                popularLeagues = filteredPopular
            )
        }
    }

    private fun confirmSelection() {
        val selectedId = _state.value.selectedLeagueId

        if (selectedId == null) {
            _state.update {
                it.copy(
                    errorMessage = "Selecciona una liga para continuar"
                )
            }
            return
        }

        // Guardamos la liga en la sesión compartida
        sessionRepository.selectLeague(selectedId)

        _state.update {
            it.copy(
                isSelectionConfirmed = true,
                errorMessage = null
            )
        }
    }
}