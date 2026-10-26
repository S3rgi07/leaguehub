package com.leaguehub.app.data.repository

import com.leaguehub.app.data.remote.LeagueHubApi

class CommunityRepository(
    private val api: LeagueHubApi
) {
    suspend fun loadAnnouncement(): Result<String> = runCatching {
        api.getCommunityAnnouncement().title
            .replaceFirstChar { it.uppercase() }
    }
}
