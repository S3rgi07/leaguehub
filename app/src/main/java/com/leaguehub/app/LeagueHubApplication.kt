package com.leaguehub.app

import android.app.Application
import com.leaguehub.app.data.repository.UserSessionRepository

class LeagueHubApplication : Application() {

    val userSessionRepository: UserSessionRepository by lazy {
        UserSessionRepository()
    }
}
