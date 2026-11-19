package com.leaguehub.app.feature.local.home

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalHomeScreen(state:LocalHomeState,onEvent:(LocalHomeEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.HOME,{onEvent(LocalHomeEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  Text("Anuncio remoto · demostración",style=MaterialTheme.typography.titleMedium)
  if(state.announcement.loading) { CircularProgressIndicator();Text("Consultando servicio de Internet…") }
  state.announcement.text?.let { Text(it,modifier=Modifier.testTag("remote-announcement")) }
  state.announcement.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
  TextButton(onClick={onEvent(LocalHomeEvent.RetryAnnouncement)},enabled=!state.announcement.loading,modifier=Modifier.testTag("network-retry")) { Text("${if(state.announcement.error!=null) "Reintentar conexión" else "Actualizar anuncio"}") }
 } }
 item { Text("Tu liga, al día",style=MaterialTheme.typography.headlineSmall) }
 if(state.leagueName==null && !state.ui.loading) item { EmptyLocal("Todavía no hay ligas en este teléfono. Un organizador puede crear la primera.");Button(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.Navigate(LocalSection.LEAGUES)))}) { Text("Ver ligas") } }
 else if(state.leagueName!=null) {
  item { LeagueHubCard(Modifier.fillMaxWidth()) { Text(state.leagueName,style=MaterialTheme.typography.titleLarge);Text(state.summary);Text(state.results);TextButton(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.Navigate(LocalSection.TABLE)))}) { Text("Ver posiciones") } } }
  item { Text("Partidos pendientes",style=MaterialTheme.typography.titleLarge) }
  if(state.upcoming.isEmpty()) item { EmptyLocal("No hay partidos pendientes.") }
  items(state.upcoming,key={it.id}) { row -> MatchCard(row,state.canManage,state.ui.busy) { onEvent(LocalHomeEvent.MatchActionSelected(row.id,it)) } }
 }

 }
}
