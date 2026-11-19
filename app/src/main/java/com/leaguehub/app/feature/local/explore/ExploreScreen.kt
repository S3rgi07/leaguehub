package com.leaguehub.app.feature.local.explore
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
@Composable fun ExploreScreen(state:ExploreState,onEvent:(ExploreEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.LEAGUES,{onEvent(ExploreEvent.Common(it))},showLeague=false,showBack=true,title="Explorar ligas") {
  item { HeroCard { LeagueBadge("TU COMUNIDAD DEPORTIVA",icon=R.drawable.lh_league);Text("Encuentra tu próxima liga",style=MaterialTheme.typography.headlineMedium);MutedText("Ligas disponibles en este teléfono. Puedes agregar otras creando una liga o importando un respaldo.") } }
  item { OutlinedTextField(value=state.query,onValueChange={onEvent(ExploreEvent.Query(it))},label={Text("Buscar liga o temporada")},modifier=Modifier.fillMaxWidth(),singleLine=true) }
  if(state.leagues.isEmpty()) item { EmptyLocal("No se encontraron ligas para esa búsqueda.") }
  items(state.leagues,key={it.id}) { row->HeroCard { Text(row.name,style=MaterialTheme.typography.titleLarge);MutedText(row.season);Button(onClick={onEvent(ExploreEvent.Open(row.id))}) { Text("Ver liga") } } }
 }
}
