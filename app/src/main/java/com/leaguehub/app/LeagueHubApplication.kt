package com.leaguehub.app

import android.app.Application
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.data.repository.UserSessionRepository
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
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
        UserSessionRepository(database.userSessionDao(), applicationScope, database.userAccountDao(), { storageErrors.tryEmit(it) })
    }

    val storageErrors = kotlinx.coroutines.flow.MutableSharedFlow<String>(extraBufferCapacity = 8)
    private val gson = com.google.gson.Gson()
    fun <T> userState(key: String, default: () -> T, encode: (T) -> String, decode: (String) -> T) =
        com.leaguehub.app.data.repository.UserScopedState(userSessionRepository, database.userDataDao(), applicationScope, key, default, encode, decode, { storageErrors.tryEmit(it) })

    val appearanceState by lazy { userState("appearance", { "Oscuro" }, { it }, { it }) }
    val permissionsState by lazy { userState("permissions", { com.leaguehub.app.feature.onboarding.permissions.PermissionsState() },
        { gson.toJson(it.copy(isCompleted = false, isSkipped = false)) },
        { gson.fromJson(it, com.leaguehub.app.feature.onboarding.permissions.PermissionsState::class.java).copy(isCompleted = false, isSkipped = false) }) }
    val notificationsState by lazy { userState("notifications", { com.leaguehub.app.feature.notifications.NotificationsState() },
        { gson.toJson(it) }, { gson.fromJson(it, com.leaguehub.app.feature.notifications.NotificationsState::class.java) }) }

    val authRepository by lazy { com.leaguehub.app.data.repository.AuthRepository(database, userSessionRepository) }

    val competitionRepository by lazy { com.leaguehub.app.data.repository.CompetitionRepository(database) }

    val communityRepository: CommunityRepository by lazy {
        CommunityRepository(NetworkModule.api)
    }

    val followedTeamsRepository: FollowedTeamsRepository by lazy {
        FollowedTeamsRepository(storage = userState("followed_teams", { emptySet<String>() },
            { gson.toJson(it.toList()) }, { gson.fromJson(it, Array<String>::class.java).toSet() }))
    }

    val matchWorkflowRepository: MatchWorkflowRepository by lazy {
        MatchWorkflowRepository(userState("workflow_falcons_titans", { com.leaguehub.app.feature.organizer.MatchWorkflowState() },
            { gson.toJson(com.leaguehub.app.feature.organizer.WorkflowSnapshot.from(it)) },
            { gson.fromJson(it, com.leaguehub.app.feature.organizer.WorkflowSnapshot::class.java).toState() }))
    }
}
