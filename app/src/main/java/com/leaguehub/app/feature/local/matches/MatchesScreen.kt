package com.leaguehub.app.feature.local.matches

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun MatchesScreen(state:MatchesState,onEvent:(MatchesEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.MATCHES,{onEvent(MatchesEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
   LeagueIconButton(R.drawable.lh_back,"Mes anterior",{onEvent(MatchesEvent.Month(-1))})
   Text(state.month.format(DateTimeFormatter.ofPattern("MMMM yyyy",Locale.forLanguageTag("es-MX"))),Modifier.weight(1f),style=MaterialTheme.typography.titleMedium)
   LeagueIconButton(R.drawable.lh_chevron,"Mes siguiente",{onEvent(MatchesEvent.Month(1))})
  }
  Row(Modifier.fillMaxWidth()) { listOf("L","M","M","J","V","S","D").forEach { Box(Modifier.weight(1f).height(24.dp),contentAlignment=Alignment.Center) { MutedText(it) } } }
  state.days.chunked(7).forEach { week->Row(Modifier.fillMaxWidth()) { week.forEach { day->
   val selected=day.date==state.selectedDate
   Column(Modifier.weight(1f).height(40.dp).selectable(selected,role=Role.Button,onClick={onEvent(MatchesEvent.Day(day.date))}).semantics { contentDescription="${day.date}${if(day.hasMatch) ", hay partido" else ""}" },horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
    Box(Modifier.size(30.dp).background(if(selected) MaterialTheme.colorScheme.primary else Color.Transparent,CircleShape),contentAlignment=Alignment.Center) { Text("${day.date.dayOfMonth}",style=MaterialTheme.typography.labelMedium,color=if(selected) MaterialTheme.colorScheme.onPrimary else if(day.inMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=.4f)) }
    Box(Modifier.size(4.dp).background(if(day.hasMatch) MaterialTheme.colorScheme.secondary else Color.Transparent,CircleShape))
   }
  } } }
  TextButton(onClick={onEvent(MatchesEvent.ShowAll)}) { Text("Ver todos los partidos") }
 } }
 item { SectionTitle("Partidos",state.canCreate,"Crear partido") { onEvent(MatchesEvent.Add) } }
 if(state.needTeams) item { Text("Agrega al menos dos equipos para programar un partido.") }
 if(state.matches.isEmpty() && !state.ui.loading) item { EmptyLocal(if(state.filtered) "No hay partidos para esta fecha." else "No hay partidos en esta liga.") }
 items(state.matches,key={it.id}) { row -> MatchCard(row,state.canManage,state.ui.busy) { onEvent(MatchesEvent.Action(row.id,it)) } }

 }
}
