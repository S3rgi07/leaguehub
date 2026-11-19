package com.leaguehub.app.feature.local.backup
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun BackupScreen(state:BackupState,onEvent:(BackupEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PROFILE,{onEvent(BackupEvent.Common(it))},showLeague=false,showBack=true,title="Respaldos") {
  item { HeroCard { LeagueIcon(R.drawable.lh_shield,null,tint=MaterialTheme.colorScheme.primary);LeagueBadge("TUS DATOS, CONTIGO");Text("Respaldos",style=MaterialTheme.typography.headlineMedium)
   Text("Incluyen tus ligas y sus datos deportivos. No incluyen cuentas ni contraseñas.")
   OutlinedButton(onClick={onEvent(BackupEvent.ChooseExport)},enabled=!state.busy && state.ui.organizer) { Text("Exportar respaldo") }
   OutlinedButton(onClick={onEvent(BackupEvent.ChooseImport)},enabled=!state.busy && state.ui.organizer) { Text("Importar respaldo") }
   if(!state.ui.organizer) Text("Cambia al modo organizador desde Perfil para usar respaldos.")
  }
  }
 }
 BackupDialogs(state,onEvent)
}
@Composable
fun BackupDialogs(state:BackupState,onEvent:(BackupEvent)->Unit) {
 val preview=state.preview
 if(state.busy && preview==null) AlertDialog(onDismissRequest={},title={Text("Procesando respaldo")},text={CircularProgressIndicator()},confirmButton={})
 else if(preview!=null) AlertDialog(onDismissRequest={onEvent(BackupEvent.Dismiss)},title={Text("Importar respaldo")},text={
  Column(Modifier.heightIn(max=360.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(10.dp)) {
   Text("${preview.leagues.size} ligas · ${preview.clubs.size} equipos · ${preview.players.size} jugadores")
   Text("${preview.fixtures.size} partidos · ${preview.events.size} eventos")
   preview.leagues.take(20).forEach { Text("${it.name} · ${it.season}") }
   Text("Se crearán copias nuevas a tu nombre, sin reemplazar registros. Los jugadores quedarán sin cuenta vinculada.")
   state.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
  }
 },confirmButton={TextButton(onClick={onEvent(BackupEvent.Confirm)},enabled=!state.busy && preview.leagues.isNotEmpty()) { Text(if(state.busy) "Importando…" else "Importar copias") }},dismissButton={TextButton(onClick={onEvent(BackupEvent.Dismiss)},enabled=!state.busy) { Text("Cancelar") }})
 else if(state.error!=null || state.message!=null) AlertDialog(onDismissRequest={onEvent(BackupEvent.Dismiss)},title={Text(if(state.error!=null) "No se pudo completar" else "Respaldo")},text={Text(state.error ?: state.message.orEmpty())},confirmButton={TextButton(onClick={onEvent(BackupEvent.Dismiss)}) { Text("Aceptar") }})
}
