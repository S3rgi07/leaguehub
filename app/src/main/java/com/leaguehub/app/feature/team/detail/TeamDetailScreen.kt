package com.leaguehub.app.feature.team.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.StandingUiModel
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme
import java.util.Locale

@Composable
fun TeamDetailScreen(
    team: TeamUiModel,
    standing: StandingUiModel?,
    nextMatch: MatchUiModel?,
    players: List<PlayerUiModel>,
    following: Boolean,
    showFullSquad: Boolean,
    onFollowToggle: () -> Unit,
    onSquadToggle: () -> Unit,
    onPlayerClick: (PlayerUiModel) -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerScaffold(PlayerDestination.HOME, onNavigate, modifier) {
        item { PlayerTopBar("", onBack = onBack) { LeagueIconButton(R.drawable.lh_share, "Compartir equipo", onShare) } }
        item {
            HeroCard {
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    TeamBadge(team, size = 88.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(team.name, style = MaterialTheme.typography.headlineLarge)
                        val description = listOfNotNull(team.foundedYear?.let { "Fundado en $it" }, team.location.takeIf { it.isNotBlank() }).joinToString(" · ")
                        if (description.isNotBlank()) MutedText(description)
                        if (standing != null) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LeagueBadge("${standing.position}º LUGAR", color = MaterialTheme.colorScheme.secondary)
                            LeagueBadge("${standing.points} PTS")
                        }
                        SecondaryAction(if (following) "Siguiendo" else "Seguir equipo", onFollowToggle, icon = if (following) R.drawable.lh_check else null)
                    }
                }
            }
        }
        item { SectionHeading("Próximo partido") }
        item {
            if (nextMatch == null) EmptyContent("No hay partidos programados para este equipo.")
            else LeagueHubCard(Modifier.fillMaxWidth().clickable(role = Role.Button) { onMatchClick(nextMatch) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TeamBadge(team)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${nextMatch.homeTeam.name} vs ${nextMatch.awayTeam.name}", style = MaterialTheme.typography.titleSmall)
                        MutedText("${nextMatch.date} · ${nextMatch.time}")
                    }
                    nextMatch.countdown?.let { LeagueBadge(it.substringBeforeLast(' ')) }
                }
            }
        }
        item { SectionHeading("Plantilla", "${players.size} JUG.", onSquadToggle) }
        if (players.isEmpty()) item { EmptyContent("La plantilla todavía no está disponible.") }
        if (!showFullSquad && players.isNotEmpty()) item {
            LeagueHubCard(Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)) {
                players.take(3).forEachIndexed { index, player ->
                    TeamPlayerRow(player, { onPlayerClick(player) })
                    if (index < players.take(3).lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
                if (players.size > 3) SecondaryAction("Ver plantilla completa", onSquadToggle, Modifier.fillMaxWidth())
            }
        }
        items(if (showFullSquad) players else emptyList(), key = { it.id }) { player ->
            LeagueHubCard(Modifier.fillMaxWidth().clickable(role = Role.Button) { onPlayerClick(player) }) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(player.number.toString(), Modifier.width(20.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                    PlayerAvatar(player.name)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(player.name, style = MaterialTheme.typography.titleSmall)
                        MutedText(player.position + if (player.isCaptain) " · CAP" else "")
                    }
                    player.rating?.let { LeagueBadge(String.format(Locale.US, "%.1f", it)) }
                }
            }
        }
        if (players.size > 3 && showFullSquad) item {
            SecondaryAction(if (showFullSquad) "Ver menos" else "Ver plantilla completa", onSquadToggle, Modifier.fillMaxWidth())
        }
    }
}
@Composable
private fun TeamPlayerRow(player: PlayerUiModel, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 60.dp).clickable(role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(player.number.toString(), Modifier.width(20.dp), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        PlayerAvatar(player.name)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(player.name, style = MaterialTheme.typography.titleSmall)
            MutedText(player.position + if (player.isCaptain) " · CAP" else "")
        }
        player.rating?.let { LeagueBadge(String.format(Locale.US, "%.1f", it)) }
    }
}

@Preview(name = "P11 · Detalle de equipo", widthDp = 390, heightDp = 844)
@Composable
private fun TeamDetailPreview() {
    LeagueHubTheme { TeamDetailScreen(falcons, fakeStandings.first { it.team.id == falcons.id }, upcomingFalconsVsTitans, fakePlayers, true, false, {}, {}, {}, {}, {}, {}, {}) }
}

@Preview(name = "P11 · Sin plantilla", widthDp = 390, heightDp = 844)
@Composable
private fun EmptyTeamPreview() {
    LeagueHubTheme { TeamDetailScreen(titans, null, null, emptyList(), false, false, {}, {}, {}, {}, {}, {}, {}) }
}
