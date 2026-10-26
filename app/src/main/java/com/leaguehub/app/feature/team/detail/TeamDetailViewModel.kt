package com.leaguehub.app.feature.team.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.feature.matches.model.MatchStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TeamDetailViewModel(
    private val teamId: String,
    private val followedTeamsRepository: FollowedTeamsRepository
) : ViewModel() {
    private val team = fakeTeams.firstOrNull { it.id == teamId } ?: falcons
    private val _state = MutableStateFlow(
        TeamDetailState(
            team = team,
            standing = fakeStandings.firstOrNull { it.team.id == team.id },
            nextMatch = fakePlayerMatches
                .filter { it.status == MatchStatus.SCHEDULED && (it.homeTeam.id == team.id || it.awayTeam.id == team.id) }
                .minByOrNull { it.localDate ?: LocalDate.MAX },
            players = fakePlayers.filter { it.teamId == team.id },
            isFollowing = followedTeamsRepository.isFollowing(team.id)
        )
    )
    val state: StateFlow<TeamDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            followedTeamsRepository.followedTeamIds.collect { followed ->
                _state.update { it.copy(isFollowing = team.id in followed) }
            }
        }
    }

    fun onEvent(event: TeamDetailEvent) {
        when (event) {
            TeamDetailEvent.FollowToggled -> followedTeamsRepository.toggle(team.id)
            TeamDetailEvent.SquadToggled -> _state.update { it.copy(showFullSquad = !it.showFullSquad) }
        }
    }
}
