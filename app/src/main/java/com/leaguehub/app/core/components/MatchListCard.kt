package com.leaguehub.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun MatchListCard(match: MatchUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val accent = when (match.status) {
        MatchStatus.SCHEDULED -> MaterialTheme.colorScheme.secondary
        MatchStatus.LIVE -> MaterialTheme.colorScheme.error
        MatchStatus.FINISHED -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    Row(
        modifier.fillMaxWidth().heightIn(min = 88.dp)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
            .clickable(role = Role.Button, onClickLabel = "Ver partido", onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(4.dp).height(76.dp).background(accent, MaterialTheme.shapes.small))
        Column(Modifier.weight(1f).padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("${match.date} · JORNADA ${match.matchday}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(match.homeTeam.name, style = MaterialTheme.typography.titleSmall)
            MutedText(match.awayTeam.name)
        }
        Column(Modifier.padding(end = 18.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                if (match.status == MatchStatus.SCHEDULED) match.time else "${match.homeScore ?: "–"} – ${match.awayScore ?: "–"}",
                style = MaterialTheme.typography.titleLarge, color = accent
            )
            MutedText(when (match.status) {
                MatchStatus.SCHEDULED -> "Próximo"
                MatchStatus.LIVE -> "En vivo · ${match.minute.orEmpty()}"
                MatchStatus.FINISHED -> "Final"
            })
        }
    }
}
