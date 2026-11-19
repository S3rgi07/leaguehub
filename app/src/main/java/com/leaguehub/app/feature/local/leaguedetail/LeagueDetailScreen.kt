package com.leaguehub.app.feature.local.leaguedetail
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
@Composable fun LeagueDetailScreen(state:LeagueDetailState,onEvent:(LeagueDetailEvent)->Unit) {
 val league=state.league
 if(league==null) { LocalFrame(state.ui,LocalSection.LEAGUES,{onEvent(LeagueDetailEvent.Common(it))},showBack=true,showLeague=false,title="Detalle de liga") { item { EmptyLocal("Liga no disponible.") } };return }
 com.leaguehub.app.feature.league.detail.LeagueDetailScreen(league,state.teams,state.news,state.tab,{onEvent(LeagueDetailEvent.Tab(it))},{onEvent(LeagueDetailEvent.Team(it.id))},{onEvent(LeagueDetailEvent.News(it.id))},{onEvent(LeagueDetailEvent.Common(LocalEvent.Back))},{onEvent(LeagueDetailEvent.Common(LocalEvent.OpenPage(LocalPage.ALERTS)))},{onEvent(LeagueDetailEvent.Common(LocalEvent.Navigate(it.localSection())))},newsLabel="Resultados")
}
