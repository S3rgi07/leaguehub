package com.leaguehub.app.feature.local.standings

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
 
 item { ChoiceTabs(listOf("General","Local","Visitante"),state.filter) { onEvent(StandingsEvent.Filter(it)) } }
 item { LeagueBadge("CLASIFICACIÓN ${listOf("GENERAL","LOCAL","VISITANTE")[state.filter]}",icon=com.leaguehub.app.R.drawable.lh_trophy) }
 item { Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface,MaterialTheme.shapes.small).padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
  Text("#   EQUIPO",Modifier.weight(1f).padding(start=8.dp),style=MaterialTheme.typography.labelSmall)
  listOf("PJ","DG","PTS").forEach { Text(it,Modifier.width(30.dp),textAlign=TextAlign.Center,style=MaterialTheme.typography.labelSmall) }
 } }
 if(state.rows.isEmpty() && !state.ui.loading) item { EmptyLocal("Agrega equipos a la liga para ver la tabla.") }
 items(state.rows,key={it.id}) { row ->
  Column {
   Row(Modifier.fillMaxWidth().padding(vertical=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    Text("${row.position}",Modifier.width(20.dp),style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.onSurfaceVariant)
    ClubCrest(row.name,Modifier.size(32.dp))
    Text(row.name,Modifier.weight(1f),style=MaterialTheme.typography.titleSmall,maxLines=1,overflow=TextOverflow.Ellipsis)
    Text("${row.played}",Modifier.width(30.dp),textAlign=TextAlign.Center,style=MaterialTheme.typography.bodySmall)
    Text("${row.difference}",Modifier.width(30.dp),textAlign=TextAlign.Center,style=MaterialTheme.typography.bodySmall)
    Text("${row.points}",Modifier.width(30.dp),textAlign=TextAlign.Center,style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary)
   }
   HorizontalDivider(color=MaterialTheme.colorScheme.outline)
  }
 }
 item { MutedText("PJ: partidos jugados · DG: diferencia de goles · PTS: puntos. Se actualiza al registrar resultados.") }

 }
}
