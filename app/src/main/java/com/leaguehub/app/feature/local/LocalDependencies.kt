package com.leaguehub.app.feature.local
import android.content.ContentResolver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.data.repository.*
data class LocalDependencies(val sessions:UserSessionRepository,val community:CommunityRepository,val backups:BackupRepository,val resolver:ContentResolver,val createStore:()->LocalHubViewModel)
@Composable fun rememberLocalDependencies():LocalDependencies {
 val app=LocalContext.current.applicationContext as LeagueHubApplication
 return remember(app) { LocalDependencies(app.userSessionRepository,app.communityRepository,BackupRepository(app.database),app.contentResolver) {
  LocalHubViewModel(app.competitionRepository,app.userSessionRepository,app.authRepository,app.database.userAccountDao(),app.followedTeamsRepository,app.appearanceState)
 } }
}
