package com.leaguehub.app.feature.local.clubs

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun ClubsScreen(state:ClubsState,onEvent:(ClubsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.CLUBS,{onEvent(ClubsEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { SectionTitle("Equipos",state.canManage,"Crear equipo") { onEvent(ClubsEvent.Add) } }
 if(state.clubs.isEmpty() && !state.ui.loading) item { EmptyLocal("No hay equipos en la liga seleccionada.") }
 items(state.clubs,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) {
  Text(row.name,style=MaterialTheme.typography.titleLarge);Text("${row.playerCount} jugadores")
  TextButton(onClick={onEvent(ClubsEvent.Follow(row.id))}) { Text(if(row.following) "Siguiendo" else "Seguir equipo") }
  TextButton(onClick={onEvent(ClubsEvent.Players(row.id))}) { Text("Ver plantilla de ${row.name}") }
  if(state.canManage) EditActions({onEvent(ClubsEvent.Edit(row.id))},{onEvent(ClubsEvent.Delete(row.id))})
 } }

 }
}
