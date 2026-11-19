package com.leaguehub.app.feature.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.*
import java.time.LocalDateTime
import java.util.UUID
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class LocalSection(val title: String) { HOME("Inicio"), LEAGUES("Ligas"), CLUBS("Equipos"), PLAYERS("Jugadores"), MATCHES("Partidos"), TABLE("Tabla"), PROFILE("Perfil") }
data class FormField(val key: String, val label: String, val value: String = "", val choices: List<Pair<String, String>> = emptyList(), val secret: Boolean = false)
data class LocalEditor(val kind: String, val id: String, val title: String, val fields: List<FormField>)
data class PendingDelete(val kind: String, val id: String, val label: String)
data class LocalState(
    val session: UserSession = UserSession(), val data: CompetitionSnapshot = CompetitionSnapshot(),
    val section: LocalSection = LocalSection.HOME, val leagueId: String? = null, val clubId: String? = null,
    val fixtureId: String? = null, val editor: LocalEditor? = null, val deletion: PendingDelete? = null,
    val loading: Boolean = true, val busy: Boolean = false, val error: String? = null, val message: String? = null,
    val followedClubIds: Set<String> = emptySet()
) {
    val league get() = data.leagues.find { it.id == leagueId }
    val clubs get() = data.clubs.filter { it.leagueId == leagueId }
    val players get() = data.players.filter { it.clubId in clubs.map { c -> c.id } }
    val fixtures get() = data.fixtures.filter { it.leagueId == leagueId }
    val fixture get() = fixtures.find { it.id == fixtureId }
    val canManage get() = session.role == SessionRole.ORGANIZER && league?.ownerId == session.userId
    val myPlayer get() = data.players.find { it.accountId == session.userId && it.accountId != null }
}

class LocalHubViewModel(
    private val repository: CompetitionRepository,
    private val sessions: UserSessionRepository,
    private val auth: AuthRepository,
    private val accounts: UserAccountDao,
    private val followed: FollowedTeamsRepository = FollowedTeamsRepository()
) : ViewModel() {
    private val mutable = MutableStateFlow(LocalState(session = sessions.session.value))
    val state = mutable.asStateFlow()
    private var loadJob: Job? = null
    init {
        viewModelScope.launch { sessions.session.collect { session -> mutable.update { it.copy(session = session) } } }
        viewModelScope.launch { followed.followedTeamIds.collect { ids -> mutable.update { it.copy(followedClubIds = ids) } } }
        reload()
    }
    fun reload() { loadJob?.cancel(); loadJob = viewModelScope.launch {
        mutable.update { it.copy(loading = true, error = null) }
        try { repository.snapshot.collect { data -> mutable.update { current ->
            val selected = current.leagueId?.takeIf { id -> data.leagues.any { it.id == id } }
                ?: sessions.session.value.selectedLeagueId?.takeIf { id -> data.leagues.any { it.id == id } }
                ?: data.leagues.firstOrNull()?.id
            current.copy(data = data, leagueId = selected, clubId = current.clubId?.takeIf { id -> data.clubs.any { it.id == id && it.leagueId == selected } },
                fixtureId = current.fixtureId?.takeIf { id -> data.fixtures.any { it.id == id && it.leagueId == selected } }, loading = false)
        } } } catch (cancelled: CancellationException) { throw cancelled }
        catch (_: Exception) { mutable.update { it.copy(loading = false, error = "No se pudieron leer los datos. Reintenta antes de continuar.") } }
    } }
    fun section(section: LocalSection) { if (!mutable.value.busy) mutable.update { it.copy(section = section, message = null) } }
    fun league(id: String) { sessions.selectLeague(id); mutable.update { it.copy(leagueId = id, clubId = null, fixtureId = null) } }
    fun club(id: String?) { mutable.update { it.copy(clubId = id) } }
    fun fixture(id: String?) { mutable.update { it.copy(fixtureId = id) } }
    fun follow(id: String) { if (mutable.value.data.clubs.any { it.id == id }) followed.toggle(id) }
    fun dismiss() { if (!mutable.value.busy) mutable.update { it.copy(editor = null, deletion = null, error = null) } }
    fun clearMessage() { mutable.update { it.copy(message = null, error = null) } }
    fun field(key: String, value: String) { if (!mutable.value.busy) mutable.update { state -> state.copy(error = null, editor = state.editor?.let { editor -> editor.copy(fields = editor.fields.map { if (it.key == key) it.copy(value = value) else it }) }) } }
    fun mode() { sessions.selectRole(if (mutable.value.session.role == SessionRole.ORGANIZER) SessionRole.PLAYER else SessionRole.ORGANIZER) }
    fun logout() { perform { sessions.logoutAndWait() } }
    fun delete(kind: String, id: String, label: String) { if (mutable.value.canManage) mutable.update { it.copy(deletion = PendingDelete(kind, id, label), error = null) } }
    fun confirmDelete() { val deletion = mutable.value.deletion ?: return; perform {
        repository.delete(owner(), deletion.kind, deletion.id)
    } }
    private fun owner() = requireNotNull(sessions.session.value.userId) { "Inicia sesión de nuevo" }
    private fun perform(block: suspend () -> Unit) {
        if (mutable.value.busy) return
        mutable.update { it.copy(busy = true, error = null) }
        viewModelScope.launch {
            try { block(); mutable.update { it.copy(busy = false, editor = null, deletion = null, message = "Cambios guardados") } }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutable.update { it.copy(busy = false, error = when(error) {
                is IllegalArgumentException -> error.message ?: "Revisa los datos ingresados"
                is java.time.format.DateTimeParseException -> "Fecha u hora inválida. Usa AAAA-MM-DD y HH:mm."
                is android.database.sqlite.SQLiteConstraintException -> "No se puede eliminar o guardar: hay registros relacionados. Elimina primero sus dependencias."
                else -> "No se pudo guardar. Revisa los datos y vuelve a intentarlo."
            }) } }
        }
    }
    fun edit(kind: String, id: String? = null) {
        val s = mutable.value
        if (s.loading || s.busy) return
        if (kind == "league" && id == null) { if (s.session.role != SessionRole.ORGANIZER) return }
        else if (kind !in listOf("profile", "password") && !s.canManage) return
        val clubs = s.clubs.map { it.id to it.name }
        fun f(key: String, label: String, value: String = "") = FormField(key, label, value)
        fun choice(key: String, label: String, value: String?, values: List<Pair<String,String>>) = FormField(key, label, value ?: values.firstOrNull()?.first.orEmpty(), values)
        val fields = when(kind) {
            "league" -> { val item=s.data.leagues.find { it.id==id }; listOf(f("name","Nombre de liga",item?.name.orEmpty()),f("season","Temporada",item?.season ?: "${java.time.LocalDate.now().year}")) }
            "club" -> listOf(f("name","Nombre de equipo",s.clubs.find { it.id==id }?.name.orEmpty()))
            "player" -> { val item=s.players.find { it.id==id }; listOf(f("name","Nombre del jugador",item?.name.orEmpty()), choice("club","Equipo",item?.clubId ?: s.clubId,clubs), f("number","Dorsal",item?.number?.toString().orEmpty()), f("position","Posición",item?.position.orEmpty()), f("email","Correo de cuenta (opcional)")) }
            "fixture" -> { val item=s.fixtures.find { it.id==id }; val time=item?.kickoff?.let(LocalDateTime::parse) ?: LocalDateTime.now().withSecond(0).withNano(0)
                listOf(choice("home","Local",item?.homeId,clubs),choice("away","Visitante",item?.awayId ?: clubs.getOrNull(1)?.first,clubs),f("date","Fecha (AAAA-MM-DD)",time.toLocalDate().toString()),f("time","Hora (HH:mm)",time.toLocalTime().toString()),f("venue","Cancha",item?.venue.orEmpty()),f("day","Jornada",item?.matchday?.toString() ?: "1")) }
            "result" -> { val item=s.fixtures.first { it.id==id }; listOf(f("homeScore","Goles local",item.homeScore?.toString().orEmpty()),f("awayScore","Goles visitante",item.awayScore?.toString().orEmpty())) }
            "event" -> { val item=s.data.events.find { it.id==id }; val match=s.fixture ?: return
                listOf(choice("player","Jugador",item?.playerId,s.players.filter { it.clubId==match.homeId || it.clubId==match.awayId }.map { it.id to "${it.name} · ${s.clubs.find { c -> c.id==it.clubId }?.name}" }), choice("kind","Tipo",item?.kind,listOf("GOAL" to "Gol","ASSIST" to "Asistencia","YELLOW" to "Amarilla","RED" to "Roja","APPEARANCE" to "Participación")),f("minute","Minuto",item?.minute?.toString() ?: "0"),f("minutes","Minutos jugados (participación)",item?.minutesPlayed?.toString() ?: "0"),f("rating","Valoración 0–10 (opcional)",item?.rating?.toString().orEmpty())) }
            "profile" -> listOf(f("name","Nombre completo",s.session.userName.orEmpty()))
            "password" -> listOf(FormField("current","Contraseña actual",secret=true),FormField("new","Nueva contraseña",secret=true),FormField("confirm","Repetir contraseña",secret=true))
            else -> return
        }
        val title = when(kind) { "league" -> "Liga"; "club" -> "Equipo"; "player" -> "Jugador"; "fixture" -> "Partido"; "result" -> "Resultado"; "event" -> "Evento"; "profile" -> "Mi nombre"; else -> "Cambiar contraseña" }
        mutable.update { it.copy(editor = LocalEditor(kind,id ?: UUID.randomUUID().toString(),title,fields),error=null) }
        if (kind=="player") { val accountId=s.players.find { it.id==id }?.accountId; if(accountId!=null) viewModelScope.launch {
            val email=accounts.findById(accountId)?.email.orEmpty()
            if(mutable.value.editor?.id==id) field("email",email)
        } }
    }
    fun save() {
        val s=mutable.value; val editor=s.editor ?: return
        val values=editor.fields.associate { it.key to it.value.trim() }
        fun v(key: String)=values[key].orEmpty()
        fun number(key: String)=v(key).toIntOrNull() ?: throw IllegalArgumentException("Ingresa un número entero en ${editor.fields.find { it.key==key }?.label}")
        perform {
            val owner=owner(); val leagueId=s.leagueId.orEmpty()
            when(editor.kind) {
                "league" -> { repository.saveLeague(owner,LeagueEntity(editor.id,owner,v("name"),v("season"))); league(editor.id) }
                "club" -> repository.saveClub(owner,ClubEntity(editor.id,leagueId,v("name")))
                "player" -> { val email=v("email"); val account=if(email.isBlank()) null else requireNotNull(accounts.findByEmail(email.lowercase(Locale.ROOT))) { "Primero registra en este teléfono la cuenta de ese correo" }
                    repository.savePlayer(owner,PlayerEntity(editor.id,v("club"),v("name"),number("number"),v("position"),account?.userId)) }
                "fixture" -> { val old=s.fixtures.find { it.id==editor.id }; repository.saveFixture(owner,FixtureEntity(editor.id,leagueId,v("home"),v("away"),LocalDateTime.parse("${v("date")}T${v("time")}").toString(),v("venue"),number("day"),old?.homeScore,old?.awayScore)) }
                "result" -> repository.saveFixture(owner,s.fixtures.first { it.id==editor.id }.copy(homeScore=number("homeScore"),awayScore=number("awayScore")))
                "event" -> repository.saveEvent(owner,MatchEventEntity(editor.id,requireNotNull(s.fixtureId),v("player"),v("kind"),number("minute"),number("minutes"),if(v("rating").isBlank()) null else v("rating").replace(',','.').toDoubleOrNull() ?: throw IllegalArgumentException("Valoración inválida")))
                "profile" -> auth.updateName(v("name"))
                "password" -> { val raw=editor.fields.associate { it.key to it.value }; require(raw["new"]==raw["confirm"]) { "Las contraseñas no coinciden" }; auth.changePassword(raw["current"].orEmpty(),raw["new"].orEmpty()) }
            }
        }
    }
    fun reopenResult(id: String) { perform { repository.saveFixture(owner(),mutable.value.fixtures.first { it.id==id }.copy(homeScore=null,awayScore=null)) } }
}
