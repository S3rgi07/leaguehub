package com.leaguehub.app.feature.local.events

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalEventsScreen(state:LocalEventsState,onEvent:(LocalEventsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.MATCHES,{onEvent(LocalEventsEvent.Common(it))},showLeague=false,showBack=true) {
 
 val match=state.match
 if(match==null && !state.ui.loading) item { EmptyLocal("Partido no encontrado.") }
 if(match!=null) {
  item { MatchCard(match,state.canManage,state.ui.busy) { onEvent(LocalEventsEvent.Action(it)) } }
  item { SectionTitle("Eventos del partido",state.canAdd,"Agregar evento") { onEvent(LocalEventsEvent.Add) };TextButton(onClick={onEvent(LocalEventsEvent.Common(LocalEvent.Back))}) { Text("Cerrar eventos") } }
  if(state.events.isEmpty()) item { EmptyLocal("Aún no hay eventos registrados.") }
  items(state.events,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) { Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) { LeagueIcon(R.drawable.lh_ball,null,tint=MaterialTheme.colorScheme.primary);Column { Text(row.title,style=MaterialTheme.typography.titleMedium);Text(row.player,style=MaterialTheme.typography.titleSmall);row.details?.let { MutedText(it) } } };if(state.canManage) EditActions({onEvent(LocalEventsEvent.Edit(row.id))},{onEvent(LocalEventsEvent.Delete(row.id))}) } }
 }

 }
}
