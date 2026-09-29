package com.leaguehub.app.feature.organizer.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.organizer.components.*
import com.leaguehub.app.feature.organizer.model.*
import com.leaguehub.app.ui.theme.LeagueHubTheme
import kotlin.math.roundToInt

@Composable
fun OrganizerDashboardScreen(
    leagueName: String, match: MatchUiModel, summary: MatchdaySummaryUiModel,
    pendingReports: List<PendingReportUiModel>, modifier: Modifier = Modifier,
    onOpenControl: () -> Unit = {}, onScan: () -> Unit = {},
    onReportSelected: (String) -> Unit = {}, onSectionSelected: (OrganizerSection) -> Unit = {}
) {
    OrganizerLayout("Panel de liga", "$leagueName · Jornada ${match.matchday}", OrganizerSection.DASHBOARD,
        modifier, onSectionSelected) {
        item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                MatchScoreboard(match.homeTeam, match.awayTeam, match.homeScore, match.awayScore, match.status, minute = match.minute)
                Text(match.venue, Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp), style = MaterialTheme.typography.bodySmall)
                TextButton(onOpenControl, Modifier.align(Alignment.CenterHorizontally)) { Text("Abrir control") }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("RESUMEN DE JORNADA")
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryMetric("${summary.matches}", "Partidos", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f).fillMaxHeight())
                    SummaryMetric("${summary.live}", "En vivo", MaterialTheme.colorScheme.primary, Modifier.weight(1f).fillMaxHeight())
                    SummaryMetric("${summary.pending}", "Actas pendientes", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Actas por completar", style = MaterialTheme.typography.titleMedium)
                Text("${summary.pending} ABIERTAS", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (pendingReports.isEmpty()) item {
            Text("Todo al día. No hay actas pendientes.", style = MaterialTheme.typography.bodyMedium)
        }
        items(pendingReports, key = { it.id }) { report ->
            Surface(onClick = { onReportSelected(report.id) }, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(report.title, style = MaterialTheme.typography.labelLarge)
                        Text(report.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                    }
                    Text(if (report.urgent) "URGENTE" else "PENDIENTE", style = MaterialTheme.typography.bodySmall,
                        color = if (report.urgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary)
                }
            }
        }
        item { OrganizerButton("Escanear nueva acta", onScan) }
        item {
            val progress = if (summary.totalReports > 0) (summary.published.toFloat() / summary.totalReports).coerceIn(0f, 1f) else 0f
            LeagueHubCard(Modifier.fillMaxWidth()) {
                Text("Jornada al ${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge)
                Text("${summary.published} de ${summary.totalReports} actas publicadas", style = MaterialTheme.typography.bodySmall)
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), drawStopIndicator = {})
            }
        }
    }
}

@Composable
private fun SummaryMetric(value: String, label: String, color: Color, modifier: Modifier) {
    LeagueHubCard(modifier) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
        Text(label, Modifier.padding(top = 6.dp), style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(name = "O01 · Panel del organizador", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DashboardPreview() {
    LeagueHubTheme { OrganizerDashboardScreen(universityLeague.name, falconsVsTitans, fakeMatchdaySummary, fakePendingReports) }
}

@Preview(name = "O01 · Sin actas pendientes", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EmptyDashboardPreview() {
    LeagueHubTheme { OrganizerDashboardScreen(universityLeague.name, falconsVsTitans,
        fakeMatchdaySummary.copy(pending = 0, published = 8), emptyList()) }
}
