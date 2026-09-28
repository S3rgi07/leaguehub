package com.leaguehub.app.feature.organizer.matchmanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.fakeMatchManagement
import com.leaguehub.app.feature.organizer.components.*
import com.leaguehub.app.feature.organizer.model.MatchManagementUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun MatchManagementScreen(
    data: MatchManagementUiModel, modifier: Modifier = Modifier, errorMessage: String? = null,
    onStartLive: () -> Unit = {}, onScan: () -> Unit = {}, onEdit: () -> Unit = {},
    onRosterSelected: (String) -> Unit = {}, onSectionSelected: (OrganizerSection) -> Unit = {}
) {
    val match = data.match
    OrganizerLayout("Gestión de partido", "Jornada ${match.matchday} · Partido #${data.number}",
        OrganizerSection.MATCHES, modifier, onSectionSelected) {
        item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                MatchScoreboard(match.homeTeam, match.awayTeam, match.homeScore, match.awayScore, match.status, minute = match.minute)
                Text(match.venue, Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel("DATOS DEL ENCUENTRO")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LeagueHubCard(Modifier.weight(1f)) {
                        SectionLabel("FECHA Y HORA")
                        Text("${match.date} · ${match.time}", Modifier.padding(top = 8.dp), style = MaterialTheme.typography.labelLarge)
                    }
                    LeagueHubCard(Modifier.weight(1f)) {
                        SectionLabel("ÁRBITRO")
                        Text(data.referee, Modifier.padding(top = 8.dp), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        item { Text("Plantillas", style = MaterialTheme.typography.titleMedium) }
        items(data.rosters, key = { it.team.id }) { roster ->
            Surface(onClick = { onRosterSelected(roster.team.id) }, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TeamBadge(roster.team)
                    Column(Modifier.weight(1f)) {
                        Text(roster.team.name, style = MaterialTheme.typography.labelLarge)
                        Text("${roster.starters} titulares · ${roster.substitutes} suplentes", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(if (roster.missing == 0) "COMPLETA" else "FALTA ${roster.missing}", style = MaterialTheme.typography.bodySmall,
                        color = if (roster.missing == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                }
            }
        }
        val incomplete = data.rosters.filter { it.missing > 0 }
        if (incomplete.isNotEmpty()) item {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.tertiary.copy(alpha = .12f)) {
                Text("Completa la alineación de ${incomplete.joinToString { it.team.name }} antes del inicio.",
                    Modifier.fillMaxWidth().padding(14.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        if (errorMessage != null) item {
            Text(errorMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LeagueHubButton("Iniciar control en vivo", onStartLive)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onScan, Modifier.weight(1f), shape = MaterialTheme.shapes.medium) { Text("Escanear acta") }
                    OutlinedButton(onEdit, Modifier.weight(1f), shape = MaterialTheme.shapes.medium) { Text("Editar datos") }
                }
            }
        }
    }
}

@Preview(name = "O02 · Plantilla incompleta", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ManagementPreview() {
    LeagueHubTheme { MatchManagementScreen(fakeMatchManagement) }
}

@Preview(name = "O02 · Plantillas completas", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ReadyManagementPreview() {
    LeagueHubTheme { MatchManagementScreen(fakeMatchManagement.copy(rosters = fakeMatchManagement.rosters.map { it.copy(starters = 11, missing = 0) })) }
}

@Preview(name = "O02 · Error", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ManagementErrorPreview() {
    LeagueHubTheme { MatchManagementScreen(fakeMatchManagement, errorMessage = "No se pudieron guardar los datos. Inténtalo de nuevo.") }
}
