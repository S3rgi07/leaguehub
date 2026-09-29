package com.leaguehub.app.feature.organizer.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubCard
import com.leaguehub.app.data.fake.falconsVsTitans
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.organizer.components.*
import com.leaguehub.app.feature.organizer.model.ScanStatus
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun ScanReportScreen(
    match: MatchUiModel, status: ScanStatus, homeScore: Int, awayScore: Int,
    modifier: Modifier = Modifier,
    onHomeScoreChange: (Int) -> Unit = {}, onAwayScoreChange: (Int) -> Unit = {},
    onProcess: () -> Unit = {}, onGallery: () -> Unit = {}, onRetry: () -> Unit = {},
    onSectionSelected: (OrganizerSection) -> Unit = {}
) {
    OrganizerLayout("Escanear acta", "${match.homeTeam.name} vs ${match.awayTeam.name} · Jornada ${match.matchday}",
        OrganizerSection.SCAN, modifier, onSectionSelected) {
        item {
            Text("Encuadra las cuatro esquinas del documento", style = MaterialTheme.typography.labelLarge)
            Text("Detectaremos marcador, eventos y alineaciones.", Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
        }
        item { DocumentFrame(match, status, homeScore, awayScore) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionLabel("CONFIRMACIÓN RÁPIDA")
                LeagueHubCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TeamBadge(match.homeTeam)
                            Text(match.homeTeam.name, style = MaterialTheme.typography.bodySmall)
                        }
                        ScoreStepper(homeScore, status == ScanStatus.DETECTED, onHomeScoreChange)
                        Text("–", style = MaterialTheme.typography.titleMedium)
                        ScoreStepper(awayScore, status == ScanStatus.DETECTED, onAwayScoreChange)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TeamBadge(match.awayTeam)
                            Text(match.awayTeam.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        if (status == ScanStatus.ERROR) item {
            Text("No pudimos leer el acta. Mejora la iluminación y vuelve a intentarlo.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }
        item {
            OrganizerButton(
                text = when (status) {
                    ScanStatus.PROCESSING -> "Procesando acta…"
                    ScanStatus.ERROR -> "Volver a capturar"
                    else -> "Procesar acta"
                },
                onClick = if (status == ScanStatus.ERROR) onRetry else onProcess,
                enabled = status == ScanStatus.DETECTED || status == ScanStatus.ERROR
            )
            TextButton(onClick = onGallery, modifier = Modifier.fillMaxWidth(), enabled = status != ScanStatus.PROCESSING) {
                Text("Elegir una foto de la galería", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ScoreStepper(score: Int, enabled: Boolean, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton({ onChange((score - 1).coerceAtLeast(0)) }, enabled = enabled && score > 0,
            modifier = Modifier.width(28.dp), contentPadding = PaddingValues(0.dp)) { Text("−") }
        Text("$score", style = MaterialTheme.typography.headlineMedium)
        TextButton({ onChange(score + 1) }, enabled = enabled,
            modifier = Modifier.width(28.dp), contentPadding = PaddingValues(0.dp)) { Text("+") }
    }
}

/** A Compose illustration for this visual-only delivery; no camera or OCR is started. */
@Composable
private fun DocumentFrame(match: MatchUiModel, status: ScanStatus, homeScore: Int, awayScore: Int) {
    val accent = if (status == ScanStatus.ERROR) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Box(Modifier.fillMaxWidth().aspectRatio(1.06f).clip(MaterialTheme.shapes.large)
        .background(accent.copy(alpha = .07f)).padding(20.dp), contentAlignment = Alignment.Center) {
        if (status != ScanStatus.SEARCHING) {
            Column(Modifier.fillMaxSize().padding(20.dp).rotate(-3f).background(Color(0xFFE5E8DF)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ACTA ARBITRAL", color = Color(0xFF202A2A), style = MaterialTheme.typography.labelLarge)
                HorizontalDivider(color = Color(0xFF899286))
                ReportTeamRow(match.homeTeam.name, homeScore)
                ReportTeamRow(match.awayTeam.name, awayScore)
                HorizontalDivider(color = Color(0xFF899286))
                repeat(3) { HorizontalDivider(Modifier.padding(top = 8.dp), color = Color(0xFFADB5A7)) }
            }
        } else {
            Text("Coloca el acta dentro del marco", style = MaterialTheme.typography.bodyMedium)
        }
        Canvas(Modifier.fillMaxSize()) {
            val corner = 28.dp.toPx()
            val stroke = 3.dp.toPx()
            listOf(Offset.Zero, Offset(size.width, 0f), Offset(0f, size.height), Offset(size.width, size.height)).forEach { point ->
                val dx = if (point.x == 0f) corner else -corner
                val dy = if (point.y == 0f) corner else -corner
                drawLine(accent, point, Offset(point.x + dx, point.y), stroke)
                drawLine(accent, point, Offset(point.x, point.y + dy), stroke)
            }
            if (status == ScanStatus.DETECTED || status == ScanStatus.PROCESSING)
                drawLine(accent.copy(alpha = .7f), Offset(0f, size.height * .48f), Offset(size.width, size.height * .48f), 1.dp.toPx())
        }
        if (status == ScanStatus.PROCESSING) CircularProgressIndicator(color = accent)
        Surface(Modifier.align(Alignment.BottomCenter), shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.background) {
            Text(when (status) {
                ScanStatus.SEARCHING -> "BUSCANDO DOCUMENTO"
                ScanStatus.DETECTED -> "DOCUMENTO DETECTADO"
                ScanStatus.PROCESSING -> "LEYENDO DOCUMENTO"
                ScanStatus.ERROR -> "DOCUMENTO NO LEGIBLE"
            }, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = accent)
        }
    }
}

@Composable
private fun ReportTeamRow(name: String, score: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name.uppercase(), style = MaterialTheme.typography.bodySmall, color = Color(0xFF202A2A))
        Text("$score", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF202A2A))
    }
}

@Composable
private fun ScanPreview(status: ScanStatus) {
    LeagueHubTheme { ScanReportScreen(falconsVsTitans, status, 2, 1) }
}

@Preview(name = "O03 · Acta detectada", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DetectedPreview() = ScanPreview(ScanStatus.DETECTED)

@Preview(name = "O03 · Buscando documento", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SearchingPreview() = ScanPreview(ScanStatus.SEARCHING)

@Preview(name = "O03 · Procesando", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProcessingPreview() = ScanPreview(ScanStatus.PROCESSING)

@Preview(name = "O03 · Error de lectura", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ErrorPreview() = ScanPreview(ScanStatus.ERROR)
