package com.leaguehub.app.feature.local
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.*
enum class LocalSection(val title: String) { HOME("Inicio"), LEAGUES("Ligas"), CLUBS("Equipos"), PLAYERS("Jugadores"), MATCHES("Partidos"), TABLE("Tabla"), PROFILE("Perfil") }
data class FormField(val key: String, val label: String, val value: String = "", val choices: List<Pair<String, String>> = emptyList(), val secret: Boolean = false)
data class LocalEditor(val kind: String, val id: String, val title: String, val fields: List<FormField>)
data class PendingDelete(val kind: String, val id: String, val label: String)
data class LocalState(
    val session: UserSession = UserSession(), val data: CompetitionSnapshot = CompetitionSnapshot(),
    val leagueId: String? = null, val clubId: String? = null,
    val fixtureId: String? = null, val editor: LocalEditor? = null, val deletion: PendingDelete? = null,
    val loading: Boolean = true, val busy: Boolean = false, val error: String? = null, val message: String? = null,
    val followedClubIds: Set<String> = emptySet(), val logoutConfirmation: Boolean = false, val appearance: String = "Oscuro"
) {
    val league get() = data.leagues.find { it.id == leagueId }
    val clubs get() = data.clubs.filter { it.leagueId == leagueId }
    val players get() = data.players.filter { it.clubId in clubs.map { c -> c.id } }
    val fixtures get() = data.fixtures.filter { it.leagueId == leagueId }
    val fixture get() = fixtures.find { it.id == fixtureId }
    val canManage get() = session.role == SessionRole.ORGANIZER && league?.ownerId == session.userId
    val myPlayer get() = data.players.find { it.accountId == session.userId && it.accountId != null }
}
