package com.leaguehub.app.feature.local

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication
import com.leaguehub.app.R
import com.leaguehub.app.core.components.LeagueHubCard
import com.leaguehub.app.data.session.SessionRole
import com.leaguehub.app.data.local.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LocalHubRoute(onLogout: () -> Unit) {
    val app=LocalContext.current.applicationContext as LeagueHubApplication
    val vm: LocalHubViewModel=viewModel(factory=viewModelFactory { initializer {
        LocalHubViewModel(app.competitionRepository,app.userSessionRepository,app.authRepository,app.database.userAccountDao(),app.followedTeamsRepository)
    } })
    val state by vm.state.collectAsStateWithLifecycle()
    val restored by app.userSessionRepository.restoreCompleted.collectAsStateWithLifecycle()
    val session by app.userSessionRepository.session.collectAsStateWithLifecycle()
    LaunchedEffect(restored,session.isAuthenticated) { if(restored && !session.isAuthenticated) onLogout() }
    if(!restored || state.session.userId!=session.userId) {
        Box(Modifier.fillMaxSize(),contentAlignment=androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val appearance by app.appearanceState.state.collectAsStateWithLifecycle()
    LocalHubScreen(state,vm,appearance,{ app.appearanceState.update { if(it=="Oscuro") "Claro" else "Oscuro" } })
}

@Composable
fun LocalHubScreen(s: LocalState, vm: LocalHubViewModel, appearance: String = "Oscuro", onAppearance: () -> Unit = {}) {
    val listState=rememberLazyListState()
    LaunchedEffect(s.section,s.leagueId,s.fixtureId) { listState.scrollToItem(0) }
    var logoutConfirmation by rememberSaveable { mutableStateOf(false) }
    val bottom=listOf(LocalSection.HOME,LocalSection.TABLE,LocalSection.MATCHES,LocalSection.PROFILE)
    val icons=listOf(R.drawable.lh_home,R.drawable.lh_trophy,R.drawable.lh_calendar,R.drawable.lh_user)
    Scaffold(bottomBar={ NavigationBar {
        bottom.forEachIndexed { index,section -> NavigationBarItem(selected=s.section==section,onClick={vm.section(section)},
            icon={Icon(painterResource(icons[index]),null,Modifier.size(22.dp))},label={Text(section.title)},modifier=Modifier.testTag("tab-${section.name}")) }
    } }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).testTag("local-list"),state=listState,contentPadding=PaddingValues(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            item {
                Text("LeagueHub",style=MaterialTheme.typography.headlineLarge)
                Text("Hola, ${s.session.userName ?: "Usuario"}",style=MaterialTheme.typography.titleMedium)
                Text(if(s.session.role==SessionRole.ORGANIZER) "Organizador" else "Jugador / aficionado",color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item { LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                items(listOf(LocalSection.LEAGUES,LocalSection.CLUBS,LocalSection.PLAYERS)) { section ->
                    FilterChip(selected=s.section==section,onClick={vm.section(section)},label={Text(section.title)},modifier=Modifier.testTag("tab-${section.name}"))
                }
            } }
            if(s.loading) item { CircularProgressIndicator(); Text("Cargando tus datos…") }
            if(s.error!=null && s.editor==null && s.deletion==null) item { Text(s.error,color=MaterialTheme.colorScheme.error); TextButton(onClick={vm.clearMessage();vm.reload()}) { Text("Reintentar") } }
            if(s.message!=null) item { Text(s.message,color=MaterialTheme.colorScheme.primary) }
            if(s.section !in listOf(LocalSection.PROFILE,LocalSection.LEAGUES) && !s.loading) item {
                ChoiceField(FormField("league","Liga",s.leagueId.orEmpty(),s.data.leagues.map { it.id to "${it.name} · ${it.season}" }),vm::league)
            }
            when(s.section) {
                LocalSection.HOME -> {
                    item { Text("Tu liga, al día",style=MaterialTheme.typography.headlineSmall) }
                    if(s.league==null && !s.loading) item { EmptyLocal("Todavía no hay ligas en este teléfono. Un organizador puede crear la primera."); Button(onClick={vm.section(LocalSection.LEAGUES)}) { Text("Ver ligas") } }
                    else if(s.league!=null) {
                        item { LeagueHubCard(Modifier.fillMaxWidth()) {
                            Text(s.league!!.name,style=MaterialTheme.typography.titleLarge)
                            Text("${s.clubs.size} equipos · ${s.players.size} jugadores · ${s.fixtures.size} partidos")
                            Text("${s.fixtures.count { it.homeScore!=null }} resultados registrados")
                            TextButton(onClick={vm.section(LocalSection.TABLE)}) { Text("Ver posiciones") }
                        } }
                        val upcoming=s.fixtures.filter { it.homeScore==null }.sortedBy { it.kickoff }
                        item { Text("Partidos pendientes",style=MaterialTheme.typography.titleLarge) }
                        if(upcoming.isEmpty()) item { EmptyLocal("No hay partidos pendientes.") }
                        items(upcoming.take(3),key={it.id}) { match -> MatchCard(s,match,vm) }
                    }
                }
                LocalSection.LEAGUES -> {
                    item { SectionTitle("Ligas en este teléfono",s.session.role==SessionRole.ORGANIZER,"Crear liga") { vm.edit("league") } }
                    if(s.data.leagues.isEmpty() && !s.loading) item { EmptyLocal("No hay ligas. Crea una para comenzar.") }
                    items(s.data.leagues,key={it.id}) { league -> LeagueHubCard(Modifier.fillMaxWidth()) {
                        Text(league.name,style=MaterialTheme.typography.titleLarge); Text(league.season)
                        if(league.id==s.leagueId) Text("Liga seleccionada",color=MaterialTheme.colorScheme.primary)
                        TextButton(onClick={vm.league(league.id);vm.section(LocalSection.CLUBS)}) { Text("Abrir ${league.name}") }
                        if(s.session.role==SessionRole.ORGANIZER && league.ownerId==s.session.userId) Row {
                            TextButton(onClick={vm.league(league.id);vm.edit("league",league.id)}) { Text("Editar liga") }
                            TextButton(onClick={vm.league(league.id);vm.delete("league",league.id,league.name)}) { Text("Eliminar liga") }
                        }
                    } }
                }
                LocalSection.CLUBS -> {
                    item { SectionTitle("Equipos",s.canManage,"Crear equipo") { vm.edit("club") } }
                    if(s.clubs.isEmpty() && !s.loading) item { EmptyLocal("No hay equipos en la liga seleccionada.") }
                    items(s.clubs,key={it.id}) { club -> LeagueHubCard(Modifier.fillMaxWidth()) {
                        Text(club.name,style=MaterialTheme.typography.titleLarge)
                        Text("${s.players.count { it.clubId==club.id }} jugadores")
                        TextButton(onClick={vm.follow(club.id)}) { Text(if(club.id in s.followedClubIds) "Siguiendo" else "Seguir equipo") }
                        TextButton(onClick={vm.club(club.id);vm.section(LocalSection.PLAYERS)}) { Text("Ver plantilla de ${club.name}") }
                        if(s.canManage) EditActions({vm.edit("club",club.id)},{vm.delete("club",club.id,club.name)})
                    } }
                }
                LocalSection.PLAYERS -> {
                    item { SectionTitle("Jugadores",s.canManage && s.clubs.isNotEmpty(),"Crear jugador") { vm.edit("player") } }
                    if(s.clubs.isNotEmpty()) item { ChoiceField(FormField("filterClub","Equipo",s.clubId.orEmpty(),listOf("" to "Todos los equipos")+s.clubs.map { it.id to it.name }),vm::club) }
                    val players=s.players.filter { s.clubId==null || s.clubId==it.clubId || s.clubId=="" }
                    if(players.isEmpty() && !s.loading) item { EmptyLocal("No hay jugadores para esta selección.") }
                    items(players,key={it.id}) { player -> LeagueHubCard(Modifier.fillMaxWidth()) {
                        Text("#${player.number} ${player.name}",style=MaterialTheme.typography.titleLarge)
                        Text("${s.clubs.find { it.id==player.clubId }?.name} · ${player.position}")
                        PlayerNumbers(s,player)
                        if(player.accountId==s.session.userId) Text("Tu jugador",color=MaterialTheme.colorScheme.primary)
                        if(s.canManage) EditActions({vm.edit("player",player.id)},{vm.delete("player",player.id,player.name)})
                    } }
                }
                LocalSection.MATCHES -> {
                    item { SectionTitle("Partidos",s.canManage && s.clubs.size>=2,"Crear partido") { vm.edit("fixture") } }
                    if(s.clubs.size<2 && s.canManage) item { Text("Agrega al menos dos equipos para programar un partido.") }
                    if(s.fixtures.isEmpty() && !s.loading) item { EmptyLocal("No hay partidos en esta liga.") }
                    items(s.fixture?.let { listOf(it) } ?: s.fixtures,key={it.id}) { match -> MatchCard(s,match,vm) }
                    s.fixture?.let { match ->
                        item { HorizontalDivider(); SectionTitle("Eventos del partido",s.canManage && s.players.any { it.clubId==match.homeId || it.clubId==match.awayId },"Agregar evento") { vm.edit("event") }
                            Text(matchName(s,match)); TextButton(onClick={vm.fixture(null)}) { Text("Cerrar eventos") } }
                        val events=s.data.events.filter { it.fixtureId==match.id }
                        if(events.isEmpty()) item { EmptyLocal("Aún no hay eventos registrados.") }
                        items(events,key={"event-${it.id}"}) { event -> LeagueHubCard(Modifier.fillMaxWidth()) {
                            Text("${event.minute}′ · ${eventTitle(event.kind)}",style=MaterialTheme.typography.titleMedium)
                            Text(s.data.players.find { it.id==event.playerId }?.name.orEmpty())
                            if(event.kind=="APPEARANCE") Text("${event.minutesPlayed} minutos · valoración ${event.rating ?: "—"}")
                            if(s.canManage) EditActions({vm.edit("event",event.id)},{vm.delete("event",event.id,eventTitle(event.kind))})
                        } }
                    }
                }
                LocalSection.TABLE -> {
                    item { Text("Tabla de posiciones",style=MaterialTheme.typography.headlineSmall); Text("PJ · G · E · P · GF:GC · DG · PTS",style=MaterialTheme.typography.bodySmall) }
                    val rows=s.leagueId?.let(s.data::table).orEmpty()
                    if(rows.isEmpty() && !s.loading) item { EmptyLocal("Agrega equipos a la liga para ver la tabla.") }
                    items(rows,key={it.club.id}) { row -> LeagueHubCard(Modifier.fillMaxWidth()) {
                        Text("${rows.indexOf(row)+1}. ${row.club.name}",style=MaterialTheme.typography.titleLarge)
                        Text("${row.played} · ${row.won} · ${row.drawn} · ${row.lost} · ${row.goalsFor}:${row.goalsAgainst} · ${row.difference} · ${row.points} PTS")
                    } }
                }
                LocalSection.PROFILE -> {
                    item { Text(s.session.userName ?: "Mi perfil",style=MaterialTheme.typography.headlineMedium); Text(s.session.email.orEmpty()) }
                    item { LeagueHubCard(Modifier.fillMaxWidth()) {
                        val player=s.myPlayer
                        if(player==null) Text("Sin equipo asignado. El organizador puede vincularte desde Jugadores usando el correo de tu cuenta.")
                        else { Text("#${player.number} · ${player.position}"); Text(s.data.clubs.find { it.id==player.clubId }?.name.orEmpty()); PlayerNumbers(s,player) }
                    } }
                    item { Text("Equipos seguidos",style=MaterialTheme.typography.titleLarge)
                        val followed=s.data.clubs.filter { it.id in s.followedClubIds }
                        if(followed.isEmpty()) Text("Todavía no sigues equipos.")
                        followed.forEach { club -> TextButton(onClick={vm.league(club.leagueId);vm.club(club.id);vm.section(LocalSection.PLAYERS)}) { Text(club.name) } } }
                    item { Button(onClick={vm.edit("profile")}) { Text("Editar mi nombre") }
                        TextButton(onClick={vm.edit("password")}) { Text("Cambiar contraseña") }
                        TextButton(onClick=onAppearance) { Text("Apariencia: $appearance") }
                        TextButton(onClick={vm.mode()}) { Text(if(s.session.role==SessionRole.ORGANIZER) "Cambiar a jugador" else "Cambiar a organizador") }
                        OutlinedButton(onClick={logoutConfirmation=true},enabled=!s.busy) { Text("Cerrar sesión") }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
    s.editor?.let { editor -> LocalEditorDialog(editor,s.busy,s.error,vm::field,vm::save,vm::dismiss) }
    s.deletion?.let { pending -> AlertDialog(onDismissRequest=vm::dismiss,title={Text("Eliminar ${pending.label}")},text={Column {
        Text(if(pending.kind=="fixture") "Se eliminará el partido y sus eventos. La tabla y las estadísticas se actualizarán." else "Esta acción no se puede deshacer. Los registros con dependencias no se eliminarán.")
        s.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
    }},confirmButton={TextButton(onClick=vm::confirmDelete,enabled=!s.busy) { Text("Eliminar") }},dismissButton={TextButton(onClick=vm::dismiss,enabled=!s.busy) { Text("Cancelar") }}) }
    if(logoutConfirmation) AlertDialog(onDismissRequest={logoutConfirmation=false},title={Text("Cerrar sesión")},text={Text("Tus datos permanecerán guardados en este teléfono.")},confirmButton={TextButton(onClick={logoutConfirmation=false;vm.logout()}) { Text("Salir") }},dismissButton={TextButton(onClick={logoutConfirmation=false}) { Text("Cancelar") }})
}

@Composable private fun SectionTitle(title: String, canAdd: Boolean, action: String, onAdd: () -> Unit) {
    Column { Text(title,style=MaterialTheme.typography.headlineSmall); if(canAdd) Button(onClick=onAdd) { Text(action) } }
}
@Composable private fun EmptyLocal(text: String) { LeagueHubCard(Modifier.fillMaxWidth()) { Text(text,color=MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun EditActions(edit: () -> Unit, delete: () -> Unit) { Row { TextButton(onClick=edit) { Text("Editar") }; TextButton(onClick=delete) { Text("Eliminar") } } }
@Composable private fun PlayerNumbers(s: LocalState, player: PlayerEntity) {
    val stats=s.data.totals(player.id)
    Text("${stats.goals} goles · ${stats.assists} asistencias · ${stats.matches} partidos")
    Text("${stats.yellow} amarillas · ${stats.red} rojas · ${stats.minutes} minutos")
    stats.rating?.let { Text("Valoración: ${String.format(java.util.Locale.ROOT,"%.1f",it)}") }
}
private fun matchName(s: LocalState,m: FixtureEntity)="${s.data.clubs.find { it.id==m.homeId }?.name.orEmpty()} vs ${s.data.clubs.find { it.id==m.awayId }?.name.orEmpty()}"
fun eventTitle(kind: String)=when(kind) { "GOAL" -> "Gol"; "ASSIST" -> "Asistencia"; "YELLOW" -> "Amarilla"; "RED" -> "Roja"; else -> "Participación" }
@Composable private fun MatchCard(s: LocalState,m: FixtureEntity,vm: LocalHubViewModel) {
    val context=LocalContext.current
    LeagueHubCard(Modifier.fillMaxWidth()) {
        Text(matchName(s,m),style=MaterialTheme.typography.titleLarge)
        Text(if(m.homeScore==null) "Programado" else "Resultado: ${m.homeScore} – ${m.awayScore}",color=MaterialTheme.colorScheme.primary)
        Text("Jornada ${m.matchday} · ${LocalDateTime.parse(m.kickoff).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
        Text(m.venue)
        TextButton(onClick={
            try { context.startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse("geo:0,0?q="+android.net.Uri.encode(m.venue)))) }
            catch (_: android.content.ActivityNotFoundException) { android.widget.Toast.makeText(context,"No hay una aplicación de mapas instalada.",android.widget.Toast.LENGTH_LONG).show() }
        }) { Text("Abrir ubicación") }
        TextButton(onClick={vm.fixture(m.id);vm.section(LocalSection.MATCHES)}) { Text("Ver eventos") }
        if(s.canManage) {
            Row { TextButton(onClick={vm.edit("fixture",m.id)}) { Text("Editar partido") }; TextButton(onClick={vm.edit("result",m.id)}) { Text("${if(m.homeScore==null) "Registrar" else "Corregir"} resultado") } }
            Row { if(m.homeScore!=null) TextButton(onClick={vm.reopenResult(m.id)},enabled=!s.busy) { Text("Volver a programado") }
                TextButton(onClick={vm.delete("fixture",m.id,matchName(s,m))}) { Text("Eliminar partido") } }
        }
        TextButton(onClick={
            val summary="${matchName(s,m)}\n${if(m.homeScore==null) "Programado" else "${m.homeScore} – ${m.awayScore}"}\n${m.kickoff.replace('T',' ')} · ${m.venue}"
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,summary),"Compartir partido"))
        }) { Text("Compartir partido") }
    }
}
@Composable private fun ChoiceField(field: FormField,onValue: (String) -> Unit) {
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
@Composable private fun LocalEditorDialog(editor: LocalEditor,busy: Boolean,error: String?,onField: (String,String) -> Unit,onSave: () -> Unit,onDismiss: () -> Unit) {
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
