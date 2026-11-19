package com.leaguehub.app.feature.local.profile

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalProfileScreen(state:LocalProfileState,onEvent:(LocalProfileEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PROFILE,{onEvent(LocalProfileEvent.Common(it))},showLeague=false,showBack=false) {
 
 item { Text(state.ui.name,style=MaterialTheme.typography.headlineMedium);Text(state.ui.email) }
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  val player=state.player
  if(player==null) Text("Sin equipo asignado. El organizador puede vincularte desde Jugadores usando el correo de tu cuenta.")
  else { Text("#${player.number} · ${player.position}");Text(player.team);PlayerNumbers(player) }
 } }
 item { Text("Equipos seguidos",style=MaterialTheme.typography.titleLarge)
  if(state.followed.isEmpty()) Text("Todavía no sigues equipos.")
  state.followed.forEach { row -> TextButton(onClick={onEvent(LocalProfileEvent.OpenClub(row.id))}) { Text(row.name) } }
 }
 item {
  Button(onClick={onEvent(LocalProfileEvent.EditName)}) { Text("Editar mi nombre") }
  TextButton(onClick={onEvent(LocalProfileEvent.Password)}) { Text("Cambiar contraseña") }
  TextButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.ChangeAppearance))}) { Text("Apariencia: ${state.ui.appearance}") }
  TextButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.ChangeMode))}) { Text(if(state.ui.organizer) "Cambiar a jugador" else "Cambiar a organizador") }
  OutlinedButton(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.RequestLogout))},enabled=!state.ui.busy) { Text("Cerrar sesión") }
 }
 if(state.ui.organizer) item { Button(onClick={onEvent(LocalProfileEvent.Common(LocalEvent.OpenBackups))},modifier=Modifier.testTag("open-backups")) { Text("Respaldos") } }

 }
}
