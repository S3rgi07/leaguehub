package com.leaguehub.app.feature.local.matches

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun MatchesScreen(state:MatchesState,onEvent:(MatchesEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.MATCHES,{onEvent(MatchesEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { SectionTitle("Partidos",state.canCreate,"Crear partido") { onEvent(MatchesEvent.Add) } }
 if(state.needTeams) item { Text("Agrega al menos dos equipos para programar un partido.") }
 if(state.matches.isEmpty() && !state.ui.loading) item { EmptyLocal("No hay partidos en esta liga.") }
 items(state.matches,key={it.id}) { row -> MatchCard(row,state.canManage,state.ui.busy) { onEvent(MatchesEvent.Action(row.id,it)) } }

 }
}
