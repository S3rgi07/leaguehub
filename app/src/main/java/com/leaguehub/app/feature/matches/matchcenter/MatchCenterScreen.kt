package com.leaguehub.app.feature.matches.matchcenter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.MatchScoreboard
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.matchcenter.components.*
import com.leaguehub.app.feature.matches.matchcenter.model.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun MatchCenterScreen(
    match: MatchUiModel,
    leagueName: String,
    selectedTab: MatchCenterTab,
    events: List<MatchEventUiModel>,
    lineups: List<TeamLineupUiModel>,
    selectedTeamId: String,
    statistics: List<MatchStatisticUiModel>,
    momentum: List<Float>,
    modifier: Modifier = Modifier,
    onTabSelected: (MatchCenterTab) -> Unit = {},
    onTeamSelected: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onFollowLive: () -> Unit = {}
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.safeDrawingPadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                MatchCenterHeader(leagueName, match.matchday, onBack, onShare)
            }
            item {
                MatchScoreboard(match.homeTeam, match.awayTeam, match.homeScore,
                    match.awayScore, match.status, minute = match.minute)
            }
            item { MatchCenterTabs(selectedTab, onTabSelected) }
            item {
                when (selectedTab) {
                    MatchCenterTab.SUMMARY -> MatchSummaryContent(events, onFollowLive)
                    MatchCenterTab.LINEUP -> MatchLineupContent(lineups, selectedTeamId, onTeamSelected)
                    MatchCenterTab.STATS -> MatchStatsContent(statistics, momentum)
                }
            }
        }
    }
}

@Composable
internal fun MatchCenterPreviewContent(tab: MatchCenterTab, teamId: String = falcons.id) {
    LeagueHubTheme {
        MatchCenterScreen(falconsVsTitans, universityLeague.name, tab, fakeMatchEvents,
            fakeLineups, teamId, fakeMatchStatistics, fakeMomentum)
    }
}

@Preview(name = "P02 · Resumen", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SummaryPreview() = MatchCenterPreviewContent(MatchCenterTab.SUMMARY)

@Preview(name = "P03 · Alineación Falcons", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LineupPreview() = MatchCenterPreviewContent(MatchCenterTab.LINEUP)

@Preview(name = "P03 · Alineación Titans", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AwayLineupPreview() = MatchCenterPreviewContent(MatchCenterTab.LINEUP, titans.id)

@Preview(name = "P04 · Estadísticas", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun StatsPreview() = MatchCenterPreviewContent(MatchCenterTab.STATS)

@Preview(name = "P02 · Sin eventos", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EmptySummaryPreview() = EmptyMatchPreview(MatchCenterTab.SUMMARY)

@Preview(name = "P03 · Alineación pendiente", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EmptyLineupPreview() = EmptyMatchPreview(MatchCenterTab.LINEUP)

@Preview(name = "P04 · Sin estadísticas", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EmptyStatsPreview() = EmptyMatchPreview(MatchCenterTab.STATS)

@Composable
private fun EmptyMatchPreview(tab: MatchCenterTab) {
    LeagueHubTheme {
        MatchCenterScreen(falconsVsWolves, universityLeague.name, tab, emptyList(),
            emptyList(), falcons.id, emptyList(), emptyList())
    }
}
