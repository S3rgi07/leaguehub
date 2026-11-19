package com.leaguehub.app
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.leaguehub.app.data.local.LeagueHubDatabase
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.remote.*
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.navigation.*
import com.leaguehub.app.navigation.graphs.localNavigation
import com.leaguehub.app.ui.theme.LeagueHubTheme
fun testCommunity()=CommunityRepository(object:LeagueHubApi { override suspend fun getCommunityAnnouncement()=CommunityAnnouncementDto(1,"Anuncio de prueba","contenido") })
@Composable fun LocalTestHost(store:LocalHubViewModel,sessions:UserSessionRepository,database:LeagueHubDatabase,community:CommunityRepository=testCommunity(),onNavReady:(androidx.navigation.NavHostController)->Unit={}) {
 val context=LocalContext.current
 val nav=rememberNavController()
 androidx.compose.runtime.SideEffect { onNavReady(nav) }
 val deps=LocalDependencies(sessions,community,BackupRepository(database),context.contentResolver,MatchSheetRepository(database),database.userDataDao()) { store }
 LeagueHubTheme { NavHost(navController=nav,startDestination=LocalHubDestination) { localNavigation(nav,deps) } }
}
