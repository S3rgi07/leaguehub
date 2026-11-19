package com.leaguehub.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Shared source of truth for teams followed by the current user. */
class FollowedTeamsRepository(
    initialTeamIds: Set<String> = emptySet(),
    private val storage: UserScopedState<Set<String>>? = null
) {
    private val _followedTeamIds = MutableStateFlow(initialTeamIds)
    val followedTeamIds: StateFlow<Set<String>> = storage?.state ?: _followedTeamIds.asStateFlow()
    private fun change(transform: (Set<String>) -> Set<String>) {
        storage?.update(transform) ?: _followedTeamIds.update(transform)
    }

    fun toggle(teamId: String) {
        change { current ->
            if (teamId in current) current - teamId else current + teamId
        }
    }

    fun isFollowing(teamId: String): Boolean = teamId in followedTeamIds.value

    fun clear() {
        change { emptySet() }
    }
}
