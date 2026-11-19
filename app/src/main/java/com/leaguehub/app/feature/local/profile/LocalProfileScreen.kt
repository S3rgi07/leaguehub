package com.leaguehub.app.feature.local.profile

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
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
fun LocalProfileScreen(state:LocalProfileState,onEvent:(LocalProfileEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PROFILE,{onEvent(LocalProfileEvent.Common(it))},showLeague=false,showBack=false,header=false) {

 item {
  Box(Modifier.fillMaxWidth()) {
   Column(Modifier.fillMaxWidth().padding(top=16.dp,bottom=8.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
    PlayerAvatar(state.ui.name,size=88.dp)
    Text(state.ui.name,style=MaterialTheme.typography.headlineLarge)
    MutedText(state.player?.let { "${it.team} · ${it.position}" } ?: "Sin equipo asignado")
    state.player?.let { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { LeagueBadge("#${it.number}",color=MaterialTheme.colorScheme.secondary);LeagueBadge("PLANTILLA") } }
   }
   LeagueIconButton(R.drawable.lh_settings,"Editar mi nombre",{onEvent(LocalProfileEvent.EditName)},Modifier.align(Alignment.TopEnd))
  }
 }
 item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
  StatCard("${state.player?.goals ?: 0}","Goles",R.drawable.lh_ball,Modifier.weight(1f))
  StatCard("${state.player?.assists ?: 0}","Asistencias",R.drawable.lh_spark,Modifier.weight(1f),MaterialTheme.colorScheme.secondary)
  StatCard("${state.player?.played ?: 0}","Partidos",R.drawable.lh_calendar,Modifier.weight(1f),MaterialTheme.colorScheme.onTertiaryContainer)
 } }
 item { Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
  StatCard("${state.player?.yellow ?: 0} / ${state.player?.red ?: 0}","Amarillas / rojas",R.drawable.lh_card,Modifier.weight(1f),MaterialTheme.colorScheme.tertiary)
  StatCard("${state.player?.minutes ?: 0}","Minutos jugados",R.drawable.lh_clock,Modifier.weight(1f),MaterialTheme.colorScheme.tertiary)
 } }
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  Text("FORMA · ÚLTIMOS 5",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
  if(state.player?.recentRatings.isNullOrEmpty()) MutedText("Todavía no hay valoraciones registradas.")
  else Row(Modifier.fillMaxWidth().height(82.dp),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.Bottom) {
   state.player.recentRatings.forEachIndexed { index,rating -> Column(horizontalAlignment=Alignment.CenterHorizontally) {
    Box(Modifier.width(32.dp).height((rating.coerceIn(0.0,10.0)*5).dp).background(if(index==state.player.recentRatings.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha=.22f),MaterialTheme.shapes.small))
    Text(String.format(java.util.Locale.US,"%.1f",rating),style=MaterialTheme.typography.labelSmall)
   } }
  }
 } }
 item { HeroCard {
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) { LeagueIcon(R.drawable.lh_shield,null,tint=MaterialTheme.colorScheme.primary);Column { Text(if((state.player?.scoringStreak ?: 0)>0) "En racha" else "Tu actividad en la cancha",style=MaterialTheme.typography.titleMedium);MutedText(if((state.player?.scoringStreak ?: 0)>0) "Marcaste en ${state.player?.scoringStreak} partidos consecutivos" else if(state.player!=null) "Estadísticas de tus partidos registrados" else "Vincula tu cuenta a una plantilla para registrar tu actividad.") } }
  state.player?.let { MutedText(it.stats);it.goalRank?.let { rank->LeagueBadge("TOP $rank GOLEO",color=MaterialTheme.colorScheme.tertiary,icon=R.drawable.lh_trophy) } }
 } }
 item { Text("Equipos seguidos",style=MaterialTheme.typography.titleLarge)
  if(state.followed.isEmpty()) Text("Todavía no sigues equipos.")
  state.followed.forEach { row -> TextButton(onClick={onEvent(LocalProfileEvent.OpenClub(row.id))}) { Text(row.name) } }
 }
 item { SectionHeading("Mi cuenta") }
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  MutedText(state.ui.email)
  Spacer(Modifier.height(8.dp))
  Button(onClick={onEvent(LocalProfileEvent.EditName)}) { Text("Editar mi nombre") }
  TextButton(onClick={onEvent(LocalProfileEvent.Password)}) { Text("Cambiar contraseña") }
  TextButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.ChangeAppearance))}) { Text("Apariencia: ${state.ui.appearance}") }
  TextButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.ChangeMode))}) { Text(if(state.ui.organizer) "Cambiar a jugador" else "Cambiar a organizador") }
  OutlinedButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.RequestLogout))},enabled=!state.ui.busy) { Text("Cerrar sesión") }
 } }
 if(state.ui.organizer) item { Button(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.OpenBackups))},modifier=Modifier.testTag("open-backups")) { Text("Respaldos") } }

 }
}
