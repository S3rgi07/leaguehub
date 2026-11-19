package com.leaguehub.app.feature.local.leagues

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
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
fun LeaguesScreen(state:LeaguesState,onEvent:(LeaguesEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.LEAGUES,{onEvent(LeaguesEvent.Common(it))},showLeague=false,showBack=false) {
 
 item { SectionTitle("Ligas en este teléfono",state.ui.organizer,"Crear liga") { onEvent(LeaguesEvent.Edit()) } }
 if(state.leagues.isEmpty() && !state.ui.loading) item { EmptyLocal("No hay ligas. Crea una para comenzar.") }
 items(state.leagues,key={it.id}) { row -> HeroCard {
  Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) { LeagueIcon(R.drawable.lh_league,null,Modifier.size(64.dp),MaterialTheme.colorScheme.primary);Column { Text(row.name,style=MaterialTheme.typography.headlineSmall);MutedText("TEMPORADA · ${row.season}") } }
  if(row.selected) LeagueBadge("LIGA SELECCIONADA",icon=R.drawable.lh_check)
  OutlinedButton(onClick={onEvent(LeaguesEvent.Details(row.id))}) { Text("Detalle de liga") }
  TextButton(onClick={onEvent(LeaguesEvent.Open(row.id))}) { Text("Abrir ${row.name}") }
  if(row.canEdit) Row { TextButton(onClick={onEvent(LeaguesEvent.Edit(row.id))}) { Text("Editar liga") };TextButton(onClick={onEvent(LeaguesEvent.Delete(row.id))}) { Text("Eliminar liga") } }
 } }

 }
}
