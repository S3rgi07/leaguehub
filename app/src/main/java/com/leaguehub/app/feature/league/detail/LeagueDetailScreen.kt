package com.leaguehub.app.feature.league.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.*
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class LeagueTab(val label: String) { OVERVIEW("Portada"), TEAMS("Equipos"), NEWS("Noticias") }

@Composable
fun LeagueDetailScreen(
    league: LeagueUiModel,
    teams: List<TeamUiModel>,
    news: List<LeagueNewsUiModel>,
    selectedTab: LeagueTab,
    onTabChange: (LeagueTab) -> Unit,
    onTeamClick: (TeamUiModel) -> Unit,
    onNewsClick: (LeagueNewsUiModel) -> Unit,
    onBack: () -> Unit,
    onNotificationsClick: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerScaffold(PlayerDestination.HOME, onNavigate, modifier) {
        item { PlayerTopBar("", onBack = onBack) { LeagueIconButton(R.drawable.lh_bell, "Alertas de la liga", onNotificationsClick) } }
        item {
            HeroCard {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.lh_league), "Escudo de ${league.name}", Modifier.size(72.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(league.name, style = MaterialTheme.typography.titleLarge)
                        MutedText(league.season)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LeagueBadge(if (league.isActive) "ACTIVA" else "FINALIZADA", icon = R.drawable.lh_radio)
                            LeagueBadge("${league.teamCount} EQUIPOS", color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Jornada ${league.currentMatchday} de ${league.totalMatchdays}", style = MaterialTheme.typography.labelSmall)
                    LinearProgressIndicator(
                        progress = { if (league.totalMatchdays > 0) (league.currentMatchday.toFloat() / league.totalMatchdays).coerceIn(0f, 1f) else 0f },
                        modifier = Modifier.weight(1f), trackColor = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(league.teamCount.toString(), "Equipos", R.drawable.lh_shield, Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                StatCard(league.playerCount.toString(), "Jugadores", R.drawable.lh_users, Modifier.weight(1f))
                StatCard(league.matchCount.toString(), "Partidos", R.drawable.lh_calendar, Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
            }
        }
        item { ChoiceTabs(LeagueTab.entries.map { it.label }, selectedTab.ordinal) { onTabChange(LeagueTab.entries[it]) } }
        if (selectedTab == LeagueTab.OVERVIEW) {
            item { SectionHeading("Equipos destacados", "Ver ${teams.size}", { onTabChange(LeagueTab.TEAMS) }) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(teams.take(3), key = { it.id }) { team ->
                        LeagueHubCard(Modifier.width(104.dp).clickable(role = Role.Button) { onTeamClick(team) }) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TeamBadge(team, size = 46.dp)
                                Text(team.name, style = MaterialTheme.typography.labelMedium)
                                LeagueIcon(R.drawable.lh_chevron, null, Modifier.size(18.dp), MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            }
        }
        if (selectedTab == LeagueTab.TEAMS) {
            if (teams.isEmpty()) item { EmptyContent("Aún no hay equipos inscritos.") }
            items(teams, key = { it.id }) { team ->
                LeagueHubCard(Modifier.fillMaxWidth().clickable(role = Role.Button) { onTeamClick(team) }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        TeamBadge(team)
                        Text(team.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                        LeagueIcon(R.drawable.lh_chevron, null, tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        } else {
            item { SectionHeading(if (selectedTab == LeagueTab.NEWS) "Noticias" else "Última noticia") }
            if (news.isEmpty()) item { EmptyContent("No hay noticias publicadas.") }
            items(if (selectedTab == LeagueTab.OVERVIEW) news.take(1) else news, key = { it.id }) { article ->
                LeagueHubCard(Modifier.fillMaxWidth().clickable(role = Role.Button) { onNewsClick(article) }) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Image(painterResource(R.drawable.lh_news), null, Modifier.size(72.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(article.title, style = MaterialTheme.typography.titleSmall)
                            MutedText(article.publishedLabel)
                        }
                    }
                }
            }
        }
    }
}
@Preview(name = "P10 · Detalle de liga", widthDp = 390, heightDp = 844)
@Composable
private fun LeagueDetailPreview() {
    LeagueHubTheme { LeagueDetailScreen(universityLeague, listOf(falcons, wolves, titans) + fakeTeams.filter { it.id !in setOf(falcons.id, wolves.id, titans.id) }, fakeLeagueNews, LeagueTab.OVERVIEW, {}, {}, {}, {}, {}, {}) }
}
