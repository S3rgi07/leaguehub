package com.leaguehub.app.feature.matches.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun MatchDetailScreen(
    match: MatchUiModel,
    onBack: () -> Unit,
    onOpenMap: () -> Unit,
    onStartRoute: () -> Unit,
    onShare: () -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val venue = match.venueDetails
    PlayerScaffold(PlayerDestination.CALENDAR, onNavigate, modifier) {
        item { PlayerTopBar("Detalle del partido", "Jornada ${match.matchday}", onBack) {
            LeagueIconButton(R.drawable.lh_share, "Compartir partido", onShare)
        } }
        item {
            HeroCard {
                LeagueBadge("${match.date} · ${match.time}", Modifier.align(Alignment.CenterHorizontally), icon = R.drawable.lh_calendar)
                MatchScoreboard(match.homeTeam, match.awayTeam, match.homeScore, match.awayScore, match.status,
                    minute = match.minute, countdown = match.countdown)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LeagueIcon(R.drawable.lh_location, null, tint = MaterialTheme.colorScheme.secondary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(match.venue, style = MaterialTheme.typography.titleSmall)
                        if (venue != null) MutedText(venue.address)
                    }
                }
                if (venue != null) Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    LeagueIcon(R.drawable.lh_clock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    MutedText("Acceso desde ${venue.accessTime}")
                }
            }
        }
        item { SectionHeading("Cómo llegar", "ABRIR MAPA", onOpenMap) }
        if (venue != null) item {
            Box(Modifier.fillMaxWidth().aspectRatio(350f / 178f)) {
                Image(painterResource(R.drawable.lh_map), "Vista ilustrativa de la ruta a ${match.venue}", Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                LeagueBadge("${venue.travelTime} · ${venue.distance}", Modifier.align(Alignment.TopStart).padding(12.dp), MaterialTheme.colorScheme.secondary, R.drawable.lh_route)
                LeagueIcon(R.drawable.lh_location, null,
                    Modifier.align(Alignment.Center).offset(x = 64.dp), MaterialTheme.colorScheme.primary)
            }
        } else item { EmptyContent("Consulta la ubicación de la cancha en tu aplicación de mapas.") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onStartRoute, Modifier.weight(1.6f).heightIn(min = 52.dp), shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)) {
                    LeagueIcon(R.drawable.lh_route, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Iniciar ruta", style = MaterialTheme.typography.labelLarge)
                }
                SecondaryAction("Compartir", onShare, Modifier.weight(1f), R.drawable.lh_share)
            }
        }
        if (venue != null) item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    LeagueIcon(R.drawable.lh_info, null, tint = MaterialTheme.colorScheme.secondary)
                    MutedText(venue.arrivalAdvice)
                }
            }
        }
    }
}
@Preview(name = "P09 · Programado y mapa", widthDp = 390, heightDp = 844)
@Composable
private fun MatchDetailPreview() {
    LeagueHubTheme { MatchDetailScreen(upcomingFalconsVsTitans, {}, {}, {}, {}, {}) }
}

@Preview(name = "P09 · Finalizado", widthDp = 390, heightDp = 844)
@Composable
private fun FinishedMatchPreview() {
    LeagueHubTheme { MatchDetailScreen(falconsVsNova, {}, {}, {}, {}, {}) }
}
