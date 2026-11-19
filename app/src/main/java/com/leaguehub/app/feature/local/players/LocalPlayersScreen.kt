package com.leaguehub.app.feature.local.players

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalPlayersScreen(state:LocalPlayersState,onEvent:(LocalPlayersEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PLAYERS,{onEvent(LocalPlayersEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { SectionTitle("Jugadores",state.canCreate,"Crear jugador") { onEvent(LocalPlayersEvent.Add) } }
 if(state.clubChoices.size>1) item { ChoiceField(FormField("filterClub","Equipo",state.selectedClub,state.clubChoices)) { onEvent(LocalPlayersEvent.Filter(it)) } }
 if(state.players.isEmpty() && !state.ui.loading) item { EmptyLocal("No hay jugadores para esta selección.") }
 items(state.players,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) {
  Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) { PlayerAvatar(row.name,size=48.dp);Column(Modifier.weight(1f)) { Text("#${row.number} ${row.name}",style=MaterialTheme.typography.titleMedium);MutedText("${row.team} · ${row.position}") } };Spacer(Modifier.height(10.dp));PlayerNumbers(row)
  if(row.isMe) Text("Tu jugador",color=MaterialTheme.colorScheme.primary)
  if(state.canManage) EditActions({onEvent(LocalPlayersEvent.Edit(row.id))},{onEvent(LocalPlayersEvent.Delete(row.id))})
 } }

 }
}
