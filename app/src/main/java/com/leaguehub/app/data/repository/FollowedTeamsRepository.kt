package com.leaguehub.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Shared source of truth for teams followed by the current user. */
class FollowedTeamsRepository(
    initialTeamIds: Set<String> = setOf("falcons")
) {
    private val _followedTeamIds = MutableStateFlow(initialTeamIds)
    val followedTeamIds: StateFlow<Set<String>> = _followedTeamIds.asStateFlow()

    fun toggle(teamId: String) {
        _followedTeamIds.update { current ->
            if (teamId in current) current - teamId else current + teamId
        }
    }

    fun isFollowing(teamId: String): Boolean = teamId in _followedTeamIds.value

    fun clear() {
        _followedTeamIds.value = emptySet()
    }
}
