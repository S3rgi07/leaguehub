package com.leaguehub.app.feature.local.dashboard
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
@Composable fun DashboardScreen(state:DashboardState,onEvent:(DashboardEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.HOME,{onEvent(DashboardEvent.Common(it))},showBack=true,title="Panel organizador") {
  item { HeroCard { LeagueBadge("GESTIÓN DE LIGA",color=MaterialTheme.colorScheme.tertiary,icon=R.drawable.lh_trophy);Text(state.name,style=MaterialTheme.typography.headlineSmall);MutedText("${state.played} de ${state.total} resultados publicados");LinearProgressIndicator(progress={if(state.total==0) 0f else state.played.toFloat()/state.total},modifier=Modifier.fillMaxWidth());Button(onClick={onEvent(DashboardEvent.Create)}) { Text("Gestionar partidos") } } }
  item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) { StatCard("${state.teams}","Equipos",R.drawable.lh_shield,Modifier.weight(1f));StatCard("${state.players}","Jugadores",R.drawable.lh_users,Modifier.weight(1f),MaterialTheme.colorScheme.secondary) } }
  item { SectionHeading("Actas por completar") }
  if(state.pending.isEmpty()) item { EmptyLocal("No tienes partidos pendientes de resultado en esta liga.") }
  items(state.pending,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) { Text(row.title,style=MaterialTheme.typography.titleMedium);MutedText(row.date);MutedText(row.venue);OutlinedButton(onClick={onEvent(DashboardEvent.Match(row.id))}) { Text("Abrir control del partido") } } }
 }
}
