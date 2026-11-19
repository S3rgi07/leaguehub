package com.leaguehub.app.feature.local
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import com.leaguehub.app.core.components.*
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
fun LocalFrame(ui:LocalUiState,section:LocalSection,onEvent:(LocalEvent)->Unit,showLeague:Boolean=true,showBack:Boolean=false,title:String?=null,header:Boolean=true,content:LazyListScope.()->Unit) {
    val list=rememberLazyListState()
    LaunchedEffect(section,ui.leagueId) { list.scrollToItem(0) }
    val bottom=listOf(LocalSection.HOME,LocalSection.TABLE,LocalSection.MATCHES,LocalSection.PROFILE)
    val icons=listOf(R.drawable.lh_home,R.drawable.lh_trophy,R.drawable.lh_calendar,R.drawable.lh_user)
    Scaffold(bottomBar={Column { HorizontalDivider(color=MaterialTheme.colorScheme.outline); NavigationBar(containerColor=MaterialTheme.colorScheme.background) { bottom.forEachIndexed { index,item ->
        NavigationBarItem(selected=section==item,onClick={onEvent(LocalEvent.Navigate(item))},icon={Icon(painterResource(icons[index]),null,Modifier.size(22.dp))},label={Text(item.title,style=MaterialTheme.typography.labelSmall)},colors=NavigationBarItemDefaults.colors(selectedIconColor=MaterialTheme.colorScheme.primary,selectedTextColor=MaterialTheme.colorScheme.onBackground,unselectedTextColor=MaterialTheme.colorScheme.onSurfaceVariant,indicatorColor=MaterialTheme.colorScheme.primary.copy(alpha=.14f),unselectedIconColor=MaterialTheme.colorScheme.onSurfaceVariant),modifier=Modifier.testTag("tab-${item.name}"))
    } }}}) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("local-list"),state=list,contentPadding=PaddingValues(start=20.dp,end=20.dp,top=8.dp,bottom=20.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
            if(showBack) item { TextButton(onClick={onEvent(LocalEvent.Back)}) { Text("Volver") } }
            if(header) item {
                if(section==LocalSection.HOME) Row(Modifier.fillMaxWidth().padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                    PlayerAvatar(ui.name,size=44.dp)
                    Column(Modifier.weight(1f)) { Text("Hola, ${ui.name.substringBefore(" ")}",style=MaterialTheme.typography.titleLarge);MutedText("Tu liga, al día · ${ui.roleLabel}") }
                    LeagueIconButton(R.drawable.lh_user,"Mi perfil",{onEvent(LocalEvent.Navigate(LocalSection.PROFILE))})
                } else PlayerTopBar(title ?: when(section) { LocalSection.TABLE->"Tabla de posiciones";LocalSection.MATCHES->"Calendario";else->section.title },if(section==LocalSection.PROFILE) ui.email else "LEAGUEHUB · TU COMUNIDAD DEPORTIVA")
            }
            if(ui.loading) item { CircularProgressIndicator();Text("Cargando tus datos…") }
            if(ui.error!=null && ui.editor==null && ui.deletion==null) item { Text(ui.error,color=MaterialTheme.colorScheme.error);TextButton(onClick={onEvent(LocalEvent.Retry)}) { Text("Reintentar") } }
            if(ui.message!=null) item { Text(ui.message,color=MaterialTheme.colorScheme.primary) }
            if(showLeague && !ui.loading) item { ChoiceField(FormField("league","Liga",ui.leagueId.orEmpty(),ui.leagueChoices)) { onEvent(LocalEvent.SelectLeague(it)) } }
            if(section in listOf(LocalSection.HOME,LocalSection.LEAGUES,LocalSection.CLUBS,LocalSection.PLAYERS)) item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) { items(listOf(LocalSection.LEAGUES,LocalSection.CLUBS,LocalSection.PLAYERS)) { item ->
                FilterChip(selected=section==item,onClick={onEvent(LocalEvent.Navigate(item))},label={Text(item.title)},modifier=Modifier.testTag("tab-${item.name}"))
            } } }
            content()
            if(section in listOf(LocalSection.PROFILE,LocalSection.TABLE,LocalSection.MATCHES)) item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) { items(listOf(LocalSection.LEAGUES,LocalSection.CLUBS,LocalSection.PLAYERS)) { item ->
                FilterChip(selected=section==item,onClick={onEvent(LocalEvent.Navigate(item))},label={Text(item.title)},modifier=Modifier.testTag("tab-${item.name}"))
            } } }
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
@Composable internal fun SectionTitle(title:String,canAdd:Boolean,action:String,onAdd:()->Unit) { SectionHeading(title,if(canAdd) action else null,onAdd) }
@Composable internal fun EmptyLocal(text:String) {
    LeagueHubCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(vertical=14.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
            LeagueIcon(R.drawable.lh_shield,null,tint=MaterialTheme.colorScheme.secondary)
            Text(text,color=MaterialTheme.colorScheme.onSurfaceVariant,style=MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable internal fun EditActions(edit:()->Unit,delete:()->Unit) { Row { TextButton(onClick=edit) { Text("Editar") };TextButton(onClick=delete) { Text("Eliminar") } } }
@Composable internal fun PlayerNumbers(row:PlayerRow) { Text(row.stats);Text(row.discipline);row.rating?.let { Text(it) } }
enum class MatchAction { EDIT, RESULT, EVENTS, REOPEN, DELETE, MAP, SHARE }
@Composable internal fun MatchCard(row:MatchRow,canManage:Boolean,busy:Boolean,onAction:(MatchAction)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    HeroCard {
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
            LeagueBadge(if(row.scheduled) "PRÓXIMO PARTIDO" else "FINAL",color=if(row.scheduled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
            Box {
                TextButton(onClick={expanded=true}) { Text("Opciones") }
                DropdownMenu(expanded=expanded,onDismissRequest={expanded=false}) {
                    DropdownMenuItem(text={Text("Abrir ubicación")},onClick={expanded=false;onAction(MatchAction.MAP)})
                    DropdownMenuItem(text={Text("Compartir partido")},onClick={expanded=false;onAction(MatchAction.SHARE)})
                    if(canManage) {
                        DropdownMenuItem(text={Text("Editar partido")},onClick={expanded=false;onAction(MatchAction.EDIT)})
                        if(!row.scheduled) DropdownMenuItem(text={Text("Volver a programado")},enabled=!busy,onClick={expanded=false;onAction(MatchAction.REOPEN)})
                        DropdownMenuItem(text={Text("Eliminar partido")},onClick={expanded=false;onAction(MatchAction.DELETE)})
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) { ClubCrest(row.home);Text(row.home,style=MaterialTheme.typography.titleSmall) }
            Text(row.scoreline,style=MaterialTheme.typography.headlineLarge,color=MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f),horizontalAlignment=Alignment.CenterHorizontally) { ClubCrest(row.away);Text(row.away,style=MaterialTheme.typography.titleSmall) }
        }
        MutedText(row.date)
        Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)) { LeagueIcon(R.drawable.lh_location,null,tint=MaterialTheme.colorScheme.secondary);MutedText(row.venue) }
        OutlinedButton(onClick={onAction(MatchAction.EVENTS)},modifier=Modifier.fillMaxWidth(),shape=MaterialTheme.shapes.medium,colors=ButtonDefaults.outlinedButtonColors(contentColor=MaterialTheme.colorScheme.secondary)) { Text("Ver eventos") }
        if(canManage) TextButton(onClick={onAction(MatchAction.RESULT)},modifier=Modifier.fillMaxWidth()) { Text(if(row.scheduled) "Registrar resultado" else "Corregir resultado") }
    }
}
@Composable internal fun ClubCrest(name:String,modifier:Modifier=Modifier) {
    val palette=listOf(MaterialTheme.colorScheme.secondary,MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.onTertiaryContainer,MaterialTheme.colorScheme.tertiary)
    val color=palette[Math.floorMod(name.hashCode(),palette.size)]
    Box(modifier.size(48.dp).background(Brush.linearGradient(listOf(color.copy(alpha=.6f),color.copy(alpha=.16f))),CircleShape).border(1.dp,color.copy(alpha=.4f),CircleShape),contentAlignment=Alignment.Center) {
        Text(name.take(3).uppercase(),style=MaterialTheme.typography.labelLarge)
    }
}

@Composable internal fun ChoiceField(field: FormField,onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(field.label,style=MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(onClick={expanded=true},enabled=field.choices.isNotEmpty(),shape=MaterialTheme.shapes.medium,colors=ButtonDefaults.outlinedButtonColors(contentColor=MaterialTheme.colorScheme.onSurface),modifier=Modifier.fillMaxWidth().testTag("field-${field.key}")) {
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
