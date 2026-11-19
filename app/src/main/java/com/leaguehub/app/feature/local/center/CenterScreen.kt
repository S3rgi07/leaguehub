package com.leaguehub.app.feature.local.center

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.feature.matches.matchcenter.components.MatchCenterTabs
import com.leaguehub.app.feature.matches.matchcenter.components.MatchLineupContent
import com.leaguehub.app.feature.matches.matchcenter.components.MatchStatsContent
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable fun CenterScreen(state:CenterState,onEvent:(CenterEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.MATCHES,{onEvent(CenterEvent.Common(it))},showLeague=false,showBack=true,title="Match Center") {
  val match=state.match
  if(match==null) item { EmptyLocal("Partido no disponible.") }
  else {
   item { MutedText("${state.league} · Jornada ${match.matchday}") }
   item { HeroCard { MatchScoreboard(match.homeTeam,match.awayTeam,match.homeScore,match.awayScore,match.status,minute=match.minute);MutedText("${match.date} · ${match.time} · ${match.venue}") } }
   item { MatchCenterTabs(state.tab) { onEvent(CenterEvent.Tab(it)) } }
   state.error?.let { item { Text(it,color=MaterialTheme.colorScheme.error,modifier=Modifier.testTag("center-error")) } }
   state.message?.let { item { Text(it,color=MaterialTheme.colorScheme.primary) } }
   if(state.busy) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
   when(state.tab) {
    MatchCenterTab.SUMMARY->{
     item { SectionHeading("Cronología",if(state.canManage) "Registrar eventos" else null,{onEvent(CenterEvent.Action(MatchAction.EVENTS))}) }
     if(state.events.isEmpty()) item { EmptyLocal("Todavía no hay eventos registrados en este partido.") }
     items(state.events,key={it.id}) { row->LeagueHubCard(Modifier.fillMaxWidth()) { Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) { LeagueIcon(R.drawable.lh_ball,null,tint=MaterialTheme.colorScheme.primary);Column { Text(row.title,style=MaterialTheme.typography.titleMedium);MutedText(row.player) } } } }
     if(state.canManage) {
      item { SectionHeading("Control del partido") }
      if(match.homeScore==null || match.status==com.leaguehub.app.feature.matches.model.MatchStatus.LIVE) item { LeagueHubCard(Modifier.fillMaxWidth()) {
       MutedText("Control local: el minuto se registra manualmente. Los goles se calculan a partir de los eventos.")
       Row { TextButton(onClick={onEvent(CenterEvent.Phase("LIVE"))},enabled=!state.busy) { Text("Iniciar / reanudar") };TextButton(onClick={onEvent(CenterEvent.Phase("HALFTIME"))},enabled=!state.busy) { Text("Descanso") } }
       OutlinedTextField(state.clock,{onEvent(CenterEvent.Field("clock",it))},label={Text("Minuto actual (0–130)")},singleLine=true,modifier=Modifier.fillMaxWidth())
       TextButton(onClick={onEvent(CenterEvent.SaveClock)},enabled=!state.busy) { Text("Guardar minuto") }
      } }
      item { Button(onClick={onEvent(CenterEvent.Action(MatchAction.RESULT))},enabled=!state.busy,modifier=Modifier.fillMaxWidth()) { Text(if(match.status==com.leaguehub.app.feature.matches.model.MatchStatus.FINISHED) "Corregir resultado" else "Publicar resultado final") } }
     }
    }
    MatchCenterTab.LINEUP->{
     item { SectionHeading("Jugadores en cancha",if(state.canManage) "Editar alineación" else null,{onEvent(CenterEvent.EditLineup)}) }
     item { MutedText("En cancha al minuto ${state.courtMinute}. Desliza para revisar el partido.");Slider(value=state.courtMinute.toFloat(),onValueChange={onEvent(CenterEvent.CourtMinute(it.toInt()))},valueRange=0f..130f,modifier=Modifier.testTag("court-minute")) }
     item { MatchLineupContent(state.lineups,state.selectedTeam) { onEvent(CenterEvent.Team(it)) } }
     if(state.substitutions.isNotEmpty()) item { LeagueHubCard(Modifier.fillMaxWidth()) { Text("Cambios registrados",style=MaterialTheme.typography.titleMedium);state.substitutions.forEach { MutedText(it) } } }
     if(state.canManage) {
      item { SectionHeading("Registrar sustitución") }
      item { LeagueHubCard(Modifier.fillMaxWidth()) {
       MutedText("Se aplica al último estado guardado, en orden de minuto. Puede haber reingresos.")
       ChoiceField(FormField("out","Sale",state.outId,state.players.filter { it.onCourt }.map { it.id to "#${it.number} ${it.name}" })) { onEvent(CenterEvent.Field("out",it)) }
       ChoiceField(FormField("in","Entra",state.inId,state.players.filter { !it.onCourt }.map { it.id to "#${it.number} ${it.name}" })) { onEvent(CenterEvent.Field("in",it)) }
       OutlinedTextField(state.changeMinute,{onEvent(CenterEvent.Field("minute",it))},label={Text("Minuto del cambio")},singleLine=true,modifier=Modifier.fillMaxWidth().testTag("change-minute"))
       Button(onClick={onEvent(CenterEvent.Substitute)},enabled=!state.busy && state.outId.isNotBlank() && state.inId.isNotBlank()) { Text("Guardar cambio") }
       if(state.substitutions.isNotEmpty()) TextButton(onClick={onEvent(CenterEvent.UndoChange)},enabled=!state.busy) { Text("Deshacer último cambio") }
      } }
      item { LeagueHubCard(Modifier.fillMaxWidth()) {
       Text("Minutos jugados",style=MaterialTheme.typography.titleMedium)
       MutedText("Calcula las participaciones desde los titulares y cambios de ambos equipos. Actualiza sus minutos guardados y conserva las valoraciones existentes.")
       OutlinedTextField(state.duration,{onEvent(CenterEvent.Field("duration",it))},label={Text("Duración del partido")},singleLine=true,modifier=Modifier.fillMaxWidth())
       TextButton(onClick={onEvent(CenterEvent.ApplyMinutes)},enabled=!state.busy) { Text("Calcular y guardar minutos") }
      } }
     }
    }
    MatchCenterTab.STATS->item { MatchStatsContent(state.statistics,emptyList());Spacer(Modifier.height(12.dp));MutedText("Se muestran eventos capturados. El marcador oficial se confirma al publicar el resultado.") }
    MatchCenterTab.GALLERY->{
     item { SectionHeading("Galería del partido",if(state.canManage) "Agregar foto" else null,{onEvent(CenterEvent.AddPhoto)}) }
     if(state.sheet.photos.isEmpty()) item { EmptyLocal("Todavía no hay fotos en este partido. El organizador puede elegir imágenes desde el teléfono.") }
     items(state.sheet.photos,key={it}) { uri->LeagueHubCard(Modifier.fillMaxWidth()) { MatchPhoto(uri);if(state.canManage) TextButton(onClick={onEvent(CenterEvent.RemovePhoto(uri))},enabled=!state.busy) { Text("Quitar de la galería") } } }
    }
   }
   item { Row { TextButton(onClick={onEvent(CenterEvent.Action(MatchAction.DETAIL))}) { Text("Ubicación y detalle") };TextButton(onClick={onEvent(CenterEvent.Action(MatchAction.SHARE))}) { Text("Compartir") } } }
  }
 }
 if(state.editing) AlertDialog(onDismissRequest={onEvent(CenterEvent.CancelLineup)},title={Text("Titulares y formación")},text={Column(Modifier.heightIn(max=420.dp).verticalScroll(rememberScrollState())) {
  ChoiceField(FormField("formation","Formación",state.formation,listOf("2-2" to "5 jugadores · 2-2","2-3-1" to "7 jugadores · 2-3-1","4-3-3" to "11 jugadores · 4-3-3"))) { onEvent(CenterEvent.Formation(it)) }
  MutedText("Selecciona titulares. Los demás quedarán como suplentes. El orden seleccionado define su ubicación en la cancha.")
  state.players.forEach { p->Row(Modifier.fillMaxWidth().clickable { onEvent(CenterEvent.TogglePlayer(p.id)) },verticalAlignment=Alignment.CenterVertically) { Checkbox(p.id in state.draft,{onEvent(CenterEvent.TogglePlayer(p.id))});Text("#${p.number} ${p.name}",style=MaterialTheme.typography.bodyMedium) } }
  state.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
 }},confirmButton={TextButton(onClick={onEvent(CenterEvent.SaveLineup)},enabled=!state.busy) { Text("Guardar alineación") }},dismissButton={TextButton(onClick={onEvent(CenterEvent.CancelLineup)},enabled=!state.busy) { Text("Cancelar") }})
}

@Composable private fun MatchPhoto(uri:String) {
 val resolver=LocalContext.current.contentResolver
 val bitmap by produceState<ImageBitmap?>(null,uri) { value=withContext(Dispatchers.IO) { runCatching {
  val parsed=android.net.Uri.parse(uri)
  val bounds=android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds=true }
  resolver.openInputStream(parsed)?.use { android.graphics.BitmapFactory.decodeStream(it,null,bounds) }
  var sample=1
  while(bounds.outWidth/sample>1200 || bounds.outHeight/sample>1200) sample*=2
  resolver.openInputStream(parsed)?.use { android.graphics.BitmapFactory.decodeStream(it,null,android.graphics.BitmapFactory.Options().apply { inSampleSize=sample }) }?.asImageBitmap()
 }.getOrNull() } }
 bitmap?.let { Image(it,"Foto del partido",Modifier.fillMaxWidth().heightIn(max=300.dp),contentScale=ContentScale.Fit) } ?: MutedText("Imagen no disponible. Si moviste el archivo original, vuelve a seleccionarlo.")
}
