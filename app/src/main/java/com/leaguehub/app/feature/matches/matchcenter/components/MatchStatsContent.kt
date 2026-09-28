package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubCard
import com.leaguehub.app.feature.matches.matchcenter.model.MatchStatisticUiModel

@Composable
fun MatchStatsContent(statistics: List<MatchStatisticUiModel>, momentum: List<Float>) {
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Text("Estadísticas del partido", style = MaterialTheme.typography.titleMedium)
        if (statistics.isEmpty()) Text("Estadísticas disponibles al iniciar el partido", style = MaterialTheme.typography.bodyMedium)
        statistics.forEachIndexed { index, stat ->
            val total = (stat.home + stat.away).coerceAtLeast(1).toFloat()
            val homeColor = if (index == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${stat.home}", style = MaterialTheme.typography.labelLarge)
                    Text(stat.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))
                    Text("${stat.away}", style = MaterialTheme.typography.labelLarge)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LinearProgressIndicator(progress = { stat.home / total }, modifier = Modifier.weight(1f).height(6.dp).clip(MaterialTheme.shapes.small),
                        color = homeColor, trackColor = MaterialTheme.colorScheme.surface, drawStopIndicator = {})
                    LinearProgressIndicator(progress = { stat.away / total }, modifier = Modifier.weight(1f).height(6.dp).clip(MaterialTheme.shapes.small),
                        color = MaterialTheme.colorScheme.tertiary, trackColor = MaterialTheme.colorScheme.surface, drawStopIndicator = {})
                }
            }
        }
        if (momentum.size > 1) LeagueHubCard(Modifier.fillMaxWidth()) {
            Text("MOMENTUM", style = MaterialTheme.typography.bodySmall)
            val color = MaterialTheme.colorScheme.primary
            Canvas(Modifier.fillMaxWidth().height(70.dp).padding(top = 12.dp)) {
                val path = Path()
                momentum.forEachIndexed { index, value ->
                    val x = size.width * index / (momentum.size - 1)
                    val y = size.height * (1 - value.coerceIn(0f, 1f))
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}
