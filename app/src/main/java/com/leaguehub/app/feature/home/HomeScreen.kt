package com.leaguehub.app.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.leaguehub.app.feature.home.model.MatchPredictionUiModel
import com.leaguehub.app.feature.league.model.LeagueUiModel
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.profile.model.PlayerUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun HomeScreen(
    player: PlayerUiModel,
    league: LeagueUiModel,
    nextMatch: MatchUiModel?,
    recentResults: List<MatchUiModel>,
    prediction: MatchPredictionUiModel?,
    onNavigate: (PlayerDestination) -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onLeagueClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerScaffold(PlayerDestination.HOME, onNavigate, modifier) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                PlayerAvatar(player.name, Modifier.clickable(role = Role.Button) { onNavigate(PlayerDestination.PROFILE) }, 44.dp)
                Column(Modifier.weight(1f).padding(start = 12.dp).heightIn(min = 48.dp).clickable(role = Role.Button, onClick = onLeagueClick), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Hola, ${player.name.substringBefore(' ')}", style = MaterialTheme.typography.headlineMedium)
                    MutedText("Jornada ${league.currentMatchday} · ${league.name}")
                }
                LeagueIconButton(R.drawable.lh_bell, "Notificaciones", onNotificationsClick)
            }
        }
        item {
            if (nextMatch == null) EmptyContent("Todavía no hay un próximo partido.")
            else HeroCard {
                LeagueBadge("PRÓXIMO PARTIDO", icon = R.drawable.lh_calendar)
                MatchScoreboard(nextMatch.homeTeam, nextMatch.awayTeam, nextMatch.homeScore, nextMatch.awayScore,
                    nextMatch.status, minute = nextMatch.minute, countdown = nextMatch.countdown)
                Text("${nextMatch.date} · ${nextMatch.time}", Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.titleSmall)
                Row(Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                    LeagueIcon(R.drawable.lh_location, null, Modifier.size(18.dp), MaterialTheme.colorScheme.secondary)
                    MutedText(nextMatch.venue)
                }
                SecondaryAction("Ver mapa de la cancha", { onMatchClick(nextMatch) }, Modifier.fillMaxWidth(), R.drawable.lh_route)
            }
        }
        item { SectionHeading("Resultados recientes", "Ver todos", { onNavigate(PlayerDestination.CALENDAR) }) }
        item {
            if (recentResults.isEmpty()) EmptyContent("Aún no hay resultados.")
            else LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentResults, key = { it.id }) { match ->
                    LeagueHubCard(Modifier.width(166.dp).clickable(role = Role.Button) { onMatchClick(match) }, contentPadding = PaddingValues(12.dp)) {
                        Text("FINAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        ResultTeam(match.homeTeam.name, match.homeScore)
                        ResultTeam(match.awayTeam.name, match.awayScore)
                        MutedText("Jornada ${match.matchday}")
                    }
                }
            }
        }
        if (prediction != null) item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LeagueBadge("PREVIA IA", color = MaterialTheme.colorScheme.onTertiaryContainer, icon = R.drawable.lh_spark)
                        Text(prediction.title, style = MaterialTheme.typography.titleMedium)
                        MutedText(prediction.description)
                    }
                    LeagueBadge("${prediction.probability} %")
                }
            }
        }
    }
}
@Composable
private fun ResultTeam(name: String, score: Int?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(name, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
        Text(score?.toString() ?: "–", style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(name = "P01 · Inicio", widthDp = 390, heightDp = 844)
@Composable
private fun HomePreview() {
    LeagueHubTheme { HomeScreen(sofia, universityLeague, upcomingFalconsVsTitans, fakeRecentResults, fakePrediction, {}, {}, {}, {}) }
}

@Preview(name = "P01 · Sin partidos", widthDp = 390, heightDp = 844)
@Composable
private fun HomeEmptyPreview() {
    LeagueHubTheme { HomeScreen(sofia, universityLeague, null, emptyList(), null, {}, {}, {}, {}) }
}
