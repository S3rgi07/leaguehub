package com.leaguehub.app.feature.league.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.league.model.*
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class StandingsScope(val label: String) { GENERAL("General"), HOME("Local"), AWAY("Visitante") }

@Composable
fun StandingsScreen(
    league: LeagueUiModel,
    standings: List<StandingUiModel>,
    selectedTeamId: String?,
    scope: StandingsScope,
    updatedLabel: String,
    onScopeChange: (StandingsScope) -> Unit,
    onTeamClick: (TeamUiModel) -> Unit,
    onLeagueClick: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    PlayerScaffold(PlayerDestination.STANDINGS, onNavigate, modifier) {
        item { PlayerTopBar("Tabla de posiciones", "${league.name} · ${league.season}") {
            LeagueIconButton(R.drawable.lh_filter, "Ver liga", onLeagueClick)
        } }
        item { ChoiceTabs(StandingsScope.entries.map { it.label }, scope.ordinal) { onScopeChange(StandingsScope.entries[it]) } }
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                LeagueBadge("JORNADA ${league.currentMatchday}", color = MaterialTheme.colorScheme.secondary, icon = R.drawable.lh_calendar)
                MutedText(updatedLabel)
            }
        }
        item {
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small).padding(horizontal = 12.dp, vertical = 12.dp)) {
                TableCell("#", Modifier.width(28.dp))
                TableCell("EQUIPO", Modifier.weight(1f))
                TableCell("PJ", Modifier.width(32.dp))
                TableCell("DG", Modifier.width(36.dp))
                TableCell("PTS", Modifier.width(30.dp))
            }
        }
        if (standings.isEmpty()) item { EmptyContent("Aún no hay posiciones para este filtro.") }
        items(standings, key = { it.team.id }) { row ->
            StandingRow(row, row.team.id == selectedTeamId, row.position <= 4, row.position >= 7, { onTeamClick(row.team) })
        }
        item {
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("● Clasificación", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text("● Descenso", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.weight(1f))
                MutedText("PJ · DG · Pts")
            }
        }
    }
}
@Composable
private fun TableCell(text: String, modifier: Modifier) {
    Text(text, modifier, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun StandingRow(row: StandingUiModel, selected: Boolean, qualifying: Boolean, relegation: Boolean, onClick: () -> Unit) {
    val accent = when {
        qualifying -> MaterialTheme.colorScheme.secondary
        relegation -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }
    Row(
        Modifier.fillMaxWidth().heightIn(min = 44.dp)
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = .10f) else Color.Transparent, MaterialTheme.shapes.medium)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, MaterialTheme.shapes.medium)
            .clickable(role = Role.Button, onClick = onClick).padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(3.dp).height(32.dp).background(accent, MaterialTheme.shapes.small))
        Spacer(Modifier.width(9.dp))
        Text(row.position.toString(), Modifier.width(26.dp), style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        TeamBadge(row.team, size = 32.dp)
        Column(Modifier.weight(1f).padding(start = 8.dp)) {
            Text(row.team.name, style = MaterialTheme.typography.titleSmall)
            if (selected) Text("TU EQUIPO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        TableCell(row.played.toString(), Modifier.width(32.dp))
        TableCell(if (row.goalDifference > 0) "+${row.goalDifference}" else row.goalDifference.toString(), Modifier.width(36.dp))
        Text(row.points.toString(), Modifier.width(40.dp), style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(name = "P06 · General", widthDp = 390, heightDp = 844)
@Composable
private fun StandingsPreview() {
    LeagueHubTheme { StandingsScreen(universityLeague, fakeStandings, falcons.id, StandingsScope.GENERAL, "Actualizada hace 2 min", {}, {}, {}, {}) }
}

@Preview(name = "P06 · Local", widthDp = 390, heightDp = 844)
@Composable
private fun HomeStandingsPreview() {
    LeagueHubTheme { StandingsScreen(universityLeague, fakeHomeStandings, falcons.id, StandingsScope.HOME, "Datos de muestra", {}, {}, {}, {}) }
}
