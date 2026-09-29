package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.feature.matches.matchcenter.model.*

@Composable
fun MatchSummaryContent(events: List<MatchEventUiModel>, onFollowLive: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Cronología", style = MaterialTheme.typography.titleMedium)
            Text("EN VIVO", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        }
        if (events.isEmpty()) Text("Todavía no hay eventos", style = MaterialTheme.typography.bodyMedium)
        Column {
            events.forEach { event ->
                val color = when (event.type) {
                    MatchEventType.GOAL -> MaterialTheme.colorScheme.primary
                    MatchEventType.YELLOW_CARD -> MaterialTheme.colorScheme.tertiary
                    MatchEventType.SUBSTITUTION -> MaterialTheme.colorScheme.secondary
                }
                Row(Modifier.fillMaxWidth().heightIn(min = 66.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f).padding(end = 8.dp), horizontalAlignment = Alignment.End) {
                        if (event.isHome) EventDescription(event, TextAlign.End)
                        else Text(event.minute, style = MaterialTheme.typography.bodySmall, color = color)
                    }
                    Box(Modifier.width(32.dp).height(66.dp), contentAlignment = Alignment.Center) {
                        Box(Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.outlineVariant))
                        Box(Modifier.size(30.dp).background(MaterialTheme.colorScheme.background, CircleShape)
                            .border(1.dp, color, CircleShape), contentAlignment = Alignment.Center) {
                            Text(when (event.type) { MatchEventType.GOAL -> "⚽"; MatchEventType.YELLOW_CARD -> "▯"; MatchEventType.SUBSTITUTION -> "⇄" }, color = color)
                        }
                    }
                    Column(Modifier.weight(1f).padding(start = 8.dp)) {
                        if (!event.isHome) EventDescription(event, TextAlign.Start)
                        else Text(event.minute, style = MaterialTheme.typography.bodySmall, color = color)
                    }
                }
            }
            Text("INICIO DEL PARTIDO", Modifier.fillMaxWidth().padding(top = 12.dp),
                textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f))
        }
        LeagueHubButton("Seguir relato en vivo", onFollowLive)
    }
}

@Composable
private fun EventDescription(event: MatchEventUiModel, alignment: TextAlign) {
    Text(event.title, Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge, textAlign = alignment)
    Text(event.description, Modifier.fillMaxWidth(), style = MaterialTheme.typography.bodySmall,
        textAlign = alignment, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
}
