package com.leaguehub.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    player: PlayerUiModel,
    team: TeamUiModel,
    onTeamClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerScaffold(PlayerDestination.PROFILE, onNavigate, modifier) {
        item {
            Box(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlayerAvatar(player.name, size = 88.dp)
                    Text(player.name, style = MaterialTheme.typography.headlineLarge)
                    Text("${team.name} · ${player.position}", Modifier.clickable(role = Role.Button, onClick = onTeamClick),
                        style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LeagueBadge("#${player.number}", color = MaterialTheme.colorScheme.secondary)
                        LeagueBadge(if (player.isStarter) "TITULAR" else "PLANTILLA")
                    }
                }
                LeagueIconButton(R.drawable.lh_settings, "Opciones del perfil", onSettingsClick, Modifier.align(Alignment.TopEnd))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(player.goals.toString(), "Goles", R.drawable.lh_ball, Modifier.weight(1f))
                StatCard(player.assists.toString(), "Asistencias", R.drawable.lh_spark, Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                StatCard(player.matchesPlayed.toString(), "Partidos", R.drawable.lh_calendar, Modifier.weight(1f), MaterialTheme.colorScheme.onTertiaryContainer)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${player.yellowCards} / ${player.redCards}", "Amarillas / rojas", R.drawable.lh_card, Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
                StatCard(NumberFormat.getIntegerInstance(Locale.US).format(player.minutesPlayed), "Minutos jugados", R.drawable.lh_clock, Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
            }
        }
        item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                Text("FORMA · ÚLTIMOS 5", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (player.recentRatings.isEmpty()) MutedText("Todavía no hay valoraciones.")
                else Row(Modifier.fillMaxWidth().height(82.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
                    player.recentRatings.takeLast(5).forEachIndexed { index, rating ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.semantics { contentDescription = "Partido ${index + 1}: $rating de 10" }) {
                            Box(Modifier.width(32.dp).height((rating.coerceIn(0.0, 10.0) * 5).dp).background(
                                if (index == player.recentRatings.takeLast(5).lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = .22f),
                                MaterialTheme.shapes.small))
                            Text(String.format(Locale.US, "%.1f", rating), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        if (player.scoringStreak > 0 || player.mvpAwards > 0 || player.goalRank != null) item {
            HeroCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LeagueIcon(R.drawable.lh_trophy, null, tint = MaterialTheme.colorScheme.primary)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("En racha", style = MaterialTheme.typography.titleMedium)
                        if (player.scoringStreak > 0) MutedText("Marcaste en ${player.scoringStreak} partidos consecutivos")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    player.goalRank?.let { LeagueBadge("TOP $it GOLEO", color = MaterialTheme.colorScheme.tertiary, icon = R.drawable.lh_trophy) }
                    if (player.mvpAwards > 0) LeagueBadge("MVP × ${player.mvpAwards}", color = MaterialTheme.colorScheme.onTertiaryContainer, icon = R.drawable.lh_spark)
                }
            }
        }
    }
}
@Preview(name = "P07 · Perfil", widthDp = 390, heightDp = 844)
@Composable
private fun ProfilePreview() { LeagueHubTheme { ProfileScreen(sofia, falcons, {}, {}, {}) } }

@Preview(name = "P07 · Sin estadísticas", widthDp = 390, heightDp = 844)
@Composable
private fun EmptyProfilePreview() { LeagueHubTheme { ProfileScreen(fakePlayers.last(), falcons, {}, {}, {}) } }
