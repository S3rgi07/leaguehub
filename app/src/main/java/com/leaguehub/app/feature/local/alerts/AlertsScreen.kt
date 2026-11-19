package com.leaguehub.app.feature.local.alerts
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
import androidx.compose.foundation.lazy.items
@Composable fun AlertsScreen(state:AlertsState,onEvent:(AlertsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.HOME,{onEvent(AlertsEvent.Common(it))},showLeague=false,showBack=true,title="Notificaciones") {
  item { Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) { LeagueBadge("${state.unread} NUEVAS",icon=R.drawable.lh_bell);Spacer(Modifier.weight(1f));TextButton(onClick={onEvent(AlertsEvent.MarkAll)}) { Text("Marcar todas leídas") } } }
  item { ChoiceTabs(listOf("Todas","Partidos","Liga"),state.filter) { onEvent(AlertsEvent.Filter(it)) } }
  state.error?.let { item { Text(it,color=MaterialTheme.colorScheme.error) } }
  item { MutedText("Actividad local de tu liga y equipos seguidos. Se actualiza con los datos del teléfono.") }
  if(state.rows.isEmpty()) item { EmptyLocal("No hay actividad para este filtro.") }
  items(state.rows,key={it.id}) { row->LeagueHubCard(Modifier.fillMaxWidth()) { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) { LeagueIcon(if(row.matchId==null) R.drawable.lh_trophy else R.drawable.lh_calendar,null,tint=if(row.unread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant);Column(Modifier.weight(1f)) { Text(row.title,style=MaterialTheme.typography.titleMedium);MutedText(row.body);TextButton(onClick={onEvent(AlertsEvent.Open(row.id))}) { Text(if(row.matchId==null) "Ver clasificación de liga" else "Ver partido") } };if(row.unread) LeagueBadge("•") } } }
 }
}
