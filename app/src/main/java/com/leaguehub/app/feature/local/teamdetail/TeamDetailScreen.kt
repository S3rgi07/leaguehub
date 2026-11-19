package com.leaguehub.app.feature.local.teamdetail
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
@Composable fun TeamDetailScreen(state:TeamDetailState,onEvent:(TeamDetailEvent)->Unit) {
 val team=state.team
 if(team==null) { LocalFrame(state.ui,LocalSection.CLUBS,{onEvent(TeamDetailEvent.Common(it))},showBack=true,showLeague=false,title="Detalle de equipo") { item { EmptyLocal("Equipo no disponible.") } };return }
 com.leaguehub.app.feature.team.detail.TeamDetailScreen(team,state.standing,state.next,state.players,state.following,state.full,{onEvent(TeamDetailEvent.Follow)},{onEvent(TeamDetailEvent.Full)},{onEvent(TeamDetailEvent.Player(it.id))},{onEvent(TeamDetailEvent.Match(it.id))},{onEvent(TeamDetailEvent.Common(LocalEvent.Back))},{onEvent(TeamDetailEvent.Share)},{onEvent(TeamDetailEvent.Common(LocalEvent.Navigate(it.localSection())))})
 state.selected?.let { p->AlertDialog(onDismissRequest={onEvent(TeamDetailEvent.Player(null))},title={Text(p.name)},text={Column(verticalArrangement=Arrangement.spacedBy(12.dp)) { PlayerAvatar(p.name,size=72.dp);LeagueBadge("#${p.number} · ${p.position}");Text("${p.goals} goles · ${p.assists} asistencias · ${p.matchesPlayed} partidos");MutedText("${p.yellowCards} amarillas · ${p.redCards} rojas · ${p.minutesPlayed} minutos");p.rating?.let { Text("Valoración: ${String.format(java.util.Locale.ROOT,"%.1f",it)}") } }},confirmButton={TextButton(onClick={onEvent(TeamDetailEvent.Player(null))}) { Text("Cerrar ficha") }}) }
}
