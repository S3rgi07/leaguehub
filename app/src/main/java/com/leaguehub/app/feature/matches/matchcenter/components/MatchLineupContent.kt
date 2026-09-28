package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.leaguehub.app.feature.matches.matchcenter.model.TeamLineupUiModel

@Composable
fun MatchLineupContent(lineups: List<TeamLineupUiModel>, selectedTeamId: String, onTeamSelected: (String) -> Unit) {
    val lineup = lineups.firstOrNull { it.team.id == selectedTeamId }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surface)) {
            lineups.forEach { entry ->
                TextButton({ onTeamSelected(entry.team.id) }, Modifier.weight(1f)) {
                    Text(entry.team.name, color = if (entry.team.id == selectedTeamId) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                }
            }
        }
        if (lineup == null) {
            Text("Alineación por confirmar", style = MaterialTheme.typography.bodyMedium)
        } else {
            Pitch(lineup)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Suplentes", style = MaterialTheme.typography.titleMedium)
                Text("DT · ${lineup.coach}", style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                lineup.substitutes.take(3).forEach { player ->
                    Surface(shape = CircleShape, color = lineup.team.color.copy(alpha = .3f)) {
                        Text("${player.number}", Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
                if (lineup.substitutes.size > 3) Text("+ ${lineup.substitutes.size - 3} MÁS", Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Pitch(lineup: TeamLineupUiModel) {
    val lines = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f)
    val grass = MaterialTheme.colorScheme.primary.copy(alpha = .12f)
    BoxWithConstraints(Modifier.fillMaxWidth().aspectRatio(1.1f).clip(MaterialTheme.shapes.large).background(grass).padding(12.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(lines, style = Stroke(1.dp.toPx()))
            drawLine(lines, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 1.dp.toPx())
            drawCircle(lines, size.height * .15f, style = Stroke(1.dp.toPx()))
            drawRect(lines, Offset(0f, size.height * .33f), Size(size.width * .1f, size.height * .34f), style = Stroke(1.dp.toPx()))
            drawRect(lines, Offset(size.width * .9f, size.height * .33f), Size(size.width * .1f, size.height * .34f), style = Stroke(1.dp.toPx()))
        }
        Text(lineup.formation, Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
        lineup.starters.forEach { entry ->
            val color = if (entry.player.number == 1) MaterialTheme.colorScheme.tertiary
                else if (entry.x < .4f) MaterialTheme.colorScheme.secondary
                else if (entry.x < .7f) MaterialTheme.colorScheme.primary else lineup.team.color
            Column(Modifier.offset(x = maxWidth * entry.x - 26.dp, y = maxHeight * entry.y - 20.dp).width(52.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(28.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                    Text("${entry.player.number}", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodySmall)
                }
                Text(if (entry.player.number == 10 && entry.player.name.startsWith("Sofía")) "Sofía" else entry.player.name,
                    style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
        }
    }
}
