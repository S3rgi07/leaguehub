package com.leaguehub.app

import android.app.Application
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository

class LeagueHubApplication : Application() {

    val userSessionRepository: UserSessionRepository by lazy {
        UserSessionRepository()
    }

    val followedTeamsRepository: FollowedTeamsRepository by lazy {
        FollowedTeamsRepository()
    }

    val matchWorkflowRepository: MatchWorkflowRepository by lazy {
        MatchWorkflowRepository()
    }
}
