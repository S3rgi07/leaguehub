package com.leaguehub.app.feature.local.home

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalHomeScreen(state:LocalHomeState,onEvent:(LocalHomeEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.HOME,{onEvent(LocalHomeEvent.Common(it))},showLeague=true,showBack=false) {
 

 if(state.leagueName==null && !state.ui.loading) item { HeroCard {
  LeagueBadge("BIENVENIDO A LEAGUEHUB",icon=R.drawable.lh_trophy)
  Text("Tu próxima temporada empieza aquí",style=MaterialTheme.typography.headlineMedium)
  MutedText("Crea una liga, reúne a tus equipos y lleva cada resultado contigo.")
  Button(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.Navigate(LocalSection.LEAGUES)))}) { Text("Ver ligas") }
 } }
 else if(state.leagueName!=null) {
  item { SectionHeading("En la cancha","Ver posiciones",{onEvent(LocalHomeEvent.Common(LocalEvent.Navigate(LocalSection.TABLE)))}) }
  if(state.upcoming.isEmpty()) item { HeroCard { LeagueBadge("TEMPORADA",icon=R.drawable.lh_trophy);Text("La liga sigue contigo",style=MaterialTheme.typography.headlineSmall);MutedText("No hay partidos pendientes. Consulta los últimos resultados o programa la siguiente jornada.") } }
  else item { MatchCard(state.upcoming.first(),false,state.ui.busy) { onEvent(LocalHomeEvent.MatchActionSelected(state.upcoming.first().id,it)) } }
  item { HeroCard {
   LeagueBadge(if(state.canManage) "PANEL DE TU LIGA" else "TEMPORADA EN CURSO",icon=R.drawable.lh_league)
   Text(state.summary,style=MaterialTheme.typography.titleMedium)
   MutedText(state.results)
   OutlinedButton(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.Navigate(LocalSection.MATCHES)))},modifier=Modifier.fillMaxWidth()) { Text(if(state.canManage) "Gestionar partidos" else "Ver calendario") }
  } }
  if(state.recent.isNotEmpty()) {
   item { SectionHeading("Últimos resultados") }
   item { LazyRow(horizontalArrangement=Arrangement.spacedBy(10.dp)) { items(state.recent,key={it.id}) { row ->
    LeagueHubCard(Modifier.width(188.dp)) {
     LeagueBadge("FINAL",color=MaterialTheme.colorScheme.secondary)
     Spacer(Modifier.height(8.dp))
     Text(row.home,style=MaterialTheme.typography.titleSmall)
     Text(row.scoreline,style=MaterialTheme.typography.headlineMedium,color=MaterialTheme.colorScheme.primary)
     Text(row.away,style=MaterialTheme.typography.titleSmall)
     TextButton(onClick={onEvent(LocalHomeEvent.MatchActionSelected(row.id,MatchAction.EVENTS))}) { Text("Ver eventos") }
    }
   } } }
  }
 }
 item { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
  OutlinedButton(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.OpenPage(LocalPage.EXPLORE)))}) { Text("Explorar ligas") }
  if(state.ui.organizer) TextButton(onClick={onEvent(LocalHomeEvent.Common(LocalEvent.OpenPage(LocalPage.DASHBOARD)))}) { Text("Panel organizador") }
 } }
 item { SectionHeading("Comunidad") }
 item { LeagueHubCard(Modifier.fillMaxWidth()) {
  LeagueBadge("ANUNCIO REMOTO · DEMOSTRACIÓN",color=MaterialTheme.colorScheme.secondary,icon=R.drawable.lh_news)
  Spacer(Modifier.height(8.dp))
  if(state.announcement.loading) { LinearProgressIndicator(Modifier.fillMaxWidth());MutedText("Consultando servicio de Internet…") }
  state.announcement.text?.let { Text(it,modifier=Modifier.testTag("remote-announcement")) }
  state.announcement.error?.let { MutedText(it) }
  TextButton(onClick={onEvent(LocalHomeEvent.RetryAnnouncement)},enabled=!state.announcement.loading,modifier=Modifier.testTag("network-retry")) { Text(if(state.announcement.error!=null) "Reintentar conexión" else "Actualizar anuncio") }
 } }

 }
}
