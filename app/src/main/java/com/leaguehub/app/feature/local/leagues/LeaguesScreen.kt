package com.leaguehub.app.feature.local.leagues

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LeaguesScreen(state:LeaguesState,onEvent:(LeaguesEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.LEAGUES,{onEvent(LeaguesEvent.Common(it))},showLeague=false,showBack=false) {
 
 item { SectionTitle("Ligas en este teléfono",state.ui.organizer,"Crear liga") { onEvent(LeaguesEvent.Edit()) } }
 if(state.leagues.isEmpty() && !state.ui.loading) item { EmptyLocal("No hay ligas. Crea una para comenzar.") }
 items(state.leagues,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) {
  Text(row.name,style=MaterialTheme.typography.titleLarge);Text(row.season)
  if(row.selected) Text("Liga seleccionada",color=MaterialTheme.colorScheme.primary)
  TextButton(onClick={onEvent(LeaguesEvent.Open(row.id))}) { Text("Abrir ${row.name}") }
  if(row.canEdit) Row { TextButton(onClick={onEvent(LeaguesEvent.Edit(row.id))}) { Text("Editar liga") };TextButton(onClick={onEvent(LeaguesEvent.Delete(row.id))}) { Text("Eliminar liga") } }
 } }

 }
}
