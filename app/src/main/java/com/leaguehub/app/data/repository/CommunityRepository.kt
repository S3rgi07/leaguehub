package com.leaguehub.app.data.repository

import com.leaguehub.app.data.remote.LeagueHubApi

class CommunityRepository(
    private val api: LeagueHubApi
) {
    suspend fun loadAnnouncement(): Result<String> = try {
        Result.success(
        api.getCommunityAnnouncement().title
            .replaceFirstChar { it.uppercase() })
    } catch (cancelled: kotlinx.coroutines.CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        Result.failure(error)
    }
}
