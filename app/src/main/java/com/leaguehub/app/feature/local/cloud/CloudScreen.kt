package com.leaguehub.app.feature.local.cloud
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.feature.local.*
@Composable fun CloudScreen(state:CloudState,onEvent:(CloudEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PROFILE,{onEvent(CloudEvent.Common(it))},showLeague=false,showBack=true,title="Sincronización Firebase") {
  item { HeroCard {
   LeagueBadge(if(state.sync.bound) "CUENTA CONECTADA" else "DATOS LOCALES",icon=R.drawable.lh_radio)
   Text(state.sync.message,style=MaterialTheme.typography.titleMedium,modifier=Modifier.testTag("cloud-status"))
   MutedText("${state.sync.pending} ligas con cambios pendientes")
   state.sync.lastSync?.let { MutedText("Última comunicación: ${java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT).format(java.util.Date(it))}") }
   TextButton(onClick={onEvent(CloudEvent.Retry)},enabled=!state.busy) { Text("Reintentar sincronización") }
  } }
  if(!state.sync.bound) item { LeagueHubCard(Modifier.fillMaxWidth()) {
   Text("Conectar mi cuenta",style=MaterialTheme.typography.titleLarge)
   MutedText("Usa la contraseña de ${state.ui.email}. Si la cuenta solo existe aquí, se vinculará sin borrar tus ligas.")
   OutlinedTextField(state.password,{onEvent(CloudEvent.Password(it))},label={Text("Contraseña")},singleLine=true,visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth().testTag("cloud-password"),enabled=!state.busy)
   Button(onClick={onEvent(CloudEvent.Connect)},enabled=state.sync.configured && !state.busy && state.password.isNotBlank()) { Text(if(state.busy) "Conectando…" else "Conectar Firebase") }
   if(!state.sync.configured) MutedText("Falta google-services.json en app/. Recompila tras agregar la configuración.")
  } }
  state.error?.let { item { Text(it,color=MaterialTheme.colorScheme.error,modifier=Modifier.testTag("cloud-error")) } }
  state.message?.let { item { Text(it,color=MaterialTheme.colorScheme.primary) } }
  if(state.busy) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
  items(state.sync.issues,key={it.id}) { issue->LeagueHubCard(Modifier.fillMaxWidth()) {
   Text(issue.title,style=MaterialTheme.typography.titleMedium);MutedText(issue.error.orEmpty())
   if(issue.conflict) {
    TextButton(onClick={onEvent(CloudEvent.AskResolve(Resolution(issue.id,false)))},enabled=!state.busy) { Text("Usar versión de la nube") }
    TextButton(onClick={onEvent(CloudEvent.AskResolve(Resolution(issue.id,true)))},enabled=!state.busy) { Text("Conservar versión de este teléfono") }
   }
  } }
  if(state.sync.recoveries>0) item { LeagueHubCard(Modifier.fillMaxWidth()) { Text("${state.sync.recoveries} copias de recuperación");MutedText("Al resolver conflictos se conserva una copia de la versión descartada en este teléfono.");state.sync.copies.forEach { copy->TextButton(onClick={onEvent(CloudEvent.ExportRecovery(copy.id))},enabled=!state.busy) { Text("Exportar ${copy.title} · ${java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT,java.text.DateFormat.SHORT).format(java.util.Date(copy.savedAt))}") } } } }
  item { EmptyLocal("Las ligas se comparten con los usuarios registrados de este proyecto; solo el dueño puede editar. Los datos pendientes permanecen en Room si no hay conexión. Las fotos y los permisos del dispositivo no se suben a Firestore.") }
 }
 state.confirmation?.let { choice->AlertDialog(onDismissRequest={onEvent(CloudEvent.AskResolve(null))},title={Text("Resolver versiones distintas")},text={Text(if(choice.keepLocal) "Esta versión local reemplazará la revisión de la nube cuando vuelva la conexión. La versión remota quedará en una copia local de recuperación." else "Se aplicará la revisión de la nube. Antes se guardará una copia de tus cambios locales para poder recuperarlos desde Respaldos.")},confirmButton={TextButton(onClick={onEvent(CloudEvent.ConfirmResolve)},enabled=!state.busy) { Text("Confirmar versión") }},dismissButton={TextButton(onClick={onEvent(CloudEvent.AskResolve(null))},enabled=!state.busy) { Text("Cancelar") }}) }
}
