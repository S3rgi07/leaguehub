package com.leaguehub.app.feature.local.standings

import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun StandingsScreen(state:StandingsState,onEvent:(StandingsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.TABLE,{onEvent(StandingsEvent.Common(it))},showLeague=true,showBack=false) {
 
 item { Text("Tabla de posiciones",style=MaterialTheme.typography.headlineSmall);Text("PJ · G · E · P · GF:GC · DG · PTS",style=MaterialTheme.typography.bodySmall) }
 if(state.rows.isEmpty() && !state.ui.loading) item { EmptyLocal("Agrega equipos a la liga para ver la tabla.") }
 items(state.rows,key={it.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) { Text(row.title,style=MaterialTheme.typography.titleLarge);Text(row.numbers) } }

 }
}
