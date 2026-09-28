package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MatchCenterHeader(leagueName: String, matchday: Int, onBack: () -> Unit, onShare: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp), modifier = Modifier.width(40.dp)) {
            Text("‹", style = MaterialTheme.typography.headlineMedium)
        }
        Column(Modifier.weight(1f)) {
            Text("Match Center", style = MaterialTheme.typography.headlineMedium)
            Text("$leagueName · Jornada $matchday", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
        }
        TextButton(onClick = onShare, contentPadding = PaddingValues(4.dp)) {
            Text("Compartir", style = MaterialTheme.typography.bodySmall)
        }
    }
}
