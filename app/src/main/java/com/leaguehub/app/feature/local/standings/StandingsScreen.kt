package com.leaguehub.app.feature.local.standings

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun StandingsScreen(state:StandingsState,onEvent:(StandingsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.TABLE,{onEvent(StandingsEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { LeagueBadge("CLASIFICACIÓN GENERAL",icon=com.leaguehub.app.R.drawable.lh_trophy) }
 item { Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface,MaterialTheme.shapes.small).padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
  Text("EQUIPO",Modifier.weight(1f),style=MaterialTheme.typography.labelSmall)
  listOf("PJ","DG","PTS").forEach { Text(it,Modifier.width(38.dp),style=MaterialTheme.typography.labelSmall) }
 } }
 if(state.rows.isEmpty() && !state.ui.loading) item { EmptyLocal("Agrega equipos a la liga para ver la tabla.") }
 items(state.rows,key={it.id}) { row ->
  Column {
   Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    Text("${row.position}",Modifier.width(20.dp),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
    ClubCrest(row.name,Modifier.size(32.dp))
    Text(row.title,Modifier.weight(1f),style=MaterialTheme.typography.titleSmall)
    Text("${row.played}",Modifier.width(30.dp),style=MaterialTheme.typography.bodySmall)
    Text("${row.difference}",Modifier.width(30.dp),style=MaterialTheme.typography.bodySmall)
    Text("${row.points}",Modifier.width(30.dp),style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary)
   }
   HorizontalDivider(color=MaterialTheme.colorScheme.outline)
  }
 }
 item { MutedText("PJ: partidos jugados · DG: diferencia de goles · PTS: puntos. Se actualiza al registrar resultados.") }

 }
}
