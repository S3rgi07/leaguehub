package com.leaguehub.app.feature.local
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.LeagueHubCard

@Composable
fun LocalFrame(ui:LocalUiState,section:LocalSection,onEvent:(LocalEvent)->Unit,showLeague:Boolean=true,showBack:Boolean=false,content:LazyListScope.()->Unit) {
    val list=rememberLazyListState()
    LaunchedEffect(section,ui.leagueId) { list.scrollToItem(0) }
    val bottom=listOf(LocalSection.HOME,LocalSection.TABLE,LocalSection.MATCHES,LocalSection.PROFILE)
    val icons=listOf(R.drawable.lh_home,R.drawable.lh_trophy,R.drawable.lh_calendar,R.drawable.lh_user)
    Scaffold(bottomBar={NavigationBar { bottom.forEachIndexed { index,item ->
        NavigationBarItem(selected=section==item,onClick={onEvent(LocalEvent.Navigate(item))},icon={Icon(painterResource(icons[index]),null,Modifier.size(22.dp))},label={Text(item.title)},modifier=Modifier.testTag("tab-${item.name}"))
    } }}) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("local-list"),state=list,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            if(showBack) item { TextButton(onClick={onEvent(LocalEvent.Back)}) { Text("Volver") } }
            item { Text("LeagueHub",style=MaterialTheme.typography.headlineLarge);Text("Hola, ${ui.name}",style=MaterialTheme.typography.titleMedium);Text(ui.roleLabel) }
            item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) { items(listOf(LocalSection.LEAGUES,LocalSection.CLUBS,LocalSection.PLAYERS)) { item ->
                FilterChip(selected=section==item,onClick={onEvent(LocalEvent.Navigate(item))},label={Text(item.title)},modifier=Modifier.testTag("tab-${item.name}"))
            } } }
            if(ui.loading) item { CircularProgressIndicator();Text("Cargando tus datos…") }
            if(ui.error!=null && ui.editor==null && ui.deletion==null) item { Text(ui.error,color=MaterialTheme.colorScheme.error);TextButton(onClick={onEvent(LocalEvent.Retry)}) { Text("Reintentar") } }
            if(ui.message!=null) item { Text(ui.message,color=MaterialTheme.colorScheme.primary) }
            if(showLeague && !ui.loading) item { ChoiceField(FormField("league","Liga",ui.leagueId.orEmpty(),ui.leagueChoices)) { onEvent(LocalEvent.SelectLeague(it)) } }
            content()
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
    ui.editor?.let { LocalEditorDialog(it,ui.busy,ui.error,{key,value->onEvent(LocalEvent.FieldChanged(key,value))},{onEvent(LocalEvent.Save)},{onEvent(LocalEvent.Dismiss)}) }
    ui.deletion?.let { pending -> AlertDialog(onDismissRequest={onEvent(LocalEvent.Dismiss)},title={Text("Eliminar ${pending.label}")},text={Column {
        Text(if(pending.kind=="fixture") "Se eliminará el partido y sus eventos. La tabla y las estadísticas se actualizarán." else "Esta acción no se puede deshacer. Los registros con dependencias no se eliminarán.")
        ui.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
    }},confirmButton={TextButton(onClick={onEvent(LocalEvent.ConfirmDelete)},enabled=!ui.busy,modifier=Modifier.testTag("delete-confirm")) { Text("Eliminar") }},dismissButton={TextButton(onClick={onEvent(LocalEvent.Dismiss)},enabled=!ui.busy) { Text("Cancelar") }}) }
    if(ui.logoutConfirmation) AlertDialog(onDismissRequest={onEvent(LocalEvent.CancelLogout)},title={Text("Cerrar sesión")},text={Text("Tus datos permanecerán guardados en este teléfono.")},confirmButton={TextButton(onClick={onEvent(LocalEvent.ConfirmLogout)}) { Text("Salir") }},dismissButton={TextButton(onClick={onEvent(LocalEvent.CancelLogout)}) { Text("Cancelar") }})
}
@Composable internal fun SectionTitle(title:String,canAdd:Boolean,action:String,onAdd:()->Unit) { Column { Text(title,style=MaterialTheme.typography.headlineSmall);if(canAdd) Button(onClick=onAdd) { Text(action) } } }
@Composable internal fun EmptyLocal(text:String) { LeagueHubCard(Modifier.fillMaxWidth()) { Text(text,color=MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable internal fun EditActions(edit:()->Unit,delete:()->Unit) { Row { TextButton(onClick=edit) { Text("Editar") };TextButton(onClick=delete) { Text("Eliminar") } } }
@Composable internal fun PlayerNumbers(row:PlayerRow) { Text(row.stats);Text(row.discipline);row.rating?.let { Text(it) } }
enum class MatchAction { EDIT, RESULT, EVENTS, REOPEN, DELETE, MAP, SHARE }
@Composable internal fun MatchCard(row:MatchRow,canManage:Boolean,busy:Boolean,onAction:(MatchAction)->Unit) {
    LeagueHubCard(Modifier.fillMaxWidth()) {
        Text(row.title,style=MaterialTheme.typography.titleLarge);Text(row.score,color=MaterialTheme.colorScheme.primary);Text(row.date);Text(row.venue)
        TextButton(onClick={onAction(MatchAction.MAP)}) { Text("Abrir ubicación") }
        TextButton(onClick={onAction(MatchAction.EVENTS)}) { Text("Ver eventos") }
        if(canManage) {
            Row { TextButton(onClick={onAction(MatchAction.EDIT)}) { Text("Editar partido") };TextButton(onClick={onAction(MatchAction.RESULT)}) { Text(if(row.scheduled) "Registrar resultado" else "Corregir resultado") } }
            Row { if(!row.scheduled) TextButton(onClick={onAction(MatchAction.REOPEN)},enabled=!busy) { Text("Volver a programado") };TextButton(onClick={onAction(MatchAction.DELETE)}) { Text("Eliminar partido") } }
        }
        TextButton(onClick={onAction(MatchAction.SHARE)}) { Text("Compartir partido") }
    }
}
@Composable internal fun ChoiceField(field: FormField,onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(field.label,style=MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(onClick={expanded=true},enabled=field.choices.isNotEmpty(),modifier=Modifier.fillMaxWidth().testTag("field-${field.key}")) {
                Text(field.choices.find { it.first==field.value }?.second ?: "Selecciona una opción")
            }
            DropdownMenu(expanded=expanded,onDismissRequest={expanded=false},modifier=Modifier.heightIn(max=260.dp)) {
                field.choices.forEach { (id,label) -> DropdownMenuItem(text={Text(label)},onClick={expanded=false;onValue(id)}) }
            }
        }
    }
}
@Composable internal fun LocalEditorDialog(editor: LocalEditor,busy: Boolean,error: String?,onField: (String,String) -> Unit,onSave: () -> Unit,onDismiss: () -> Unit) {
    val choices=setOf("home","away","club","player","kind")
    val scroll=rememberScrollState()
    LaunchedEffect(error) { if(error!=null) { withFrameNanos { }; scroll.animateScrollTo(scroll.maxValue) } }
    AlertDialog(onDismissRequest=onDismiss,title={Text(editor.title)},text={
        Column(Modifier.heightIn(max=440.dp).verticalScroll(scroll).imePadding(),verticalArrangement=Arrangement.spacedBy(12.dp)) {
            editor.fields.forEach { field ->
                if(field.key in choices) ChoiceField(field) { onField(field.key,it) }
                else OutlinedTextField(value=field.value,onValueChange={onField(field.key,it)},label={Text(field.label)},singleLine=true,enabled=!busy,
                    modifier=Modifier.fillMaxWidth().testTag("field-${field.key}"),visualTransformation=if(field.secret) PasswordVisualTransformation() else VisualTransformation.None)
            }
            error?.let { Text(it,color=MaterialTheme.colorScheme.error,modifier=Modifier.testTag("form-error")) }
        }
    },confirmButton={TextButton(onClick=onSave,enabled=!busy && editor.fields.none { it.key in choices && it.choices.isEmpty() },modifier=Modifier.testTag("form-save")) { Text(if(busy) "Guardando…" else "Guardar") }},
        dismissButton={TextButton(onClick=onDismiss,enabled=!busy) { Text("Cancelar") }})
}
