package com.leaguehub.app

import android.app.Application
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.data.repository.CommunityRepository
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.remote.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LeagueHubApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: LeagueHubDatabase by lazy {
        LeagueHubDatabase.getInstance(this)
    }

    val userSessionRepository: UserSessionRepository by lazy {
        UserSessionRepository(database.userSessionDao(), applicationScope)
    }

    val communityRepository: CommunityRepository by lazy {
        CommunityRepository(NetworkModule.api)
    }

    val followedTeamsRepository: FollowedTeamsRepository by lazy {
        FollowedTeamsRepository()
    }
}
