package com.leaguehub.app.data.remote

import retrofit2.http.GET

interface LeagueHubApi {
    @GET("posts/1")
    suspend fun getCommunityAnnouncement(): CommunityAnnouncementDto
}

data class CommunityAnnouncementDto(
    val id: Int,
    val title: String,
    val body: String
)
