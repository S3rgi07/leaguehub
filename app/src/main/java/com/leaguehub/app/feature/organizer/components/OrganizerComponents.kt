package com.leaguehub.app.feature.organizer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.feature.team.model.TeamUiModel

enum class OrganizerSection(val label: String, val symbol: String) {
    DASHBOARD("Panel", "⌂"), SCAN("Escanear", "▣"), MATCHES("Partidos", "▤"), SETTINGS("Gestión", "◎")
}

@Composable
fun OrganizerLayout(title: String, subtitle: String, section: OrganizerSection,
    modifier: Modifier = Modifier, onSectionSelected: (OrganizerSection) -> Unit = {},
    content: LazyListScope.() -> Unit) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.safeDrawingPadding()) {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    Text(subtitle, Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                }
                content()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.surface)
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                OrganizerSection.entries.forEach { item ->
                    TextButton({ onSectionSelected(item) }, Modifier.weight(1f), contentPadding = PaddingValues(vertical = 8.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val color = if (item == section) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
                            Text(item.symbol, style = MaterialTheme.typography.titleMedium, color = color)
                            Text(item.label, style = MaterialTheme.typography.bodySmall, color = color)
                        }
                    }
                }
            }
        }
    }
}

/** Reuses the shared button with the organizer's existing tertiary color token. */
@Composable
fun OrganizerButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.colorScheme.tertiary,
        onPrimary = MaterialTheme.colorScheme.onTertiary)) {
        LeagueHubButton(text, onClick, modifier, enabled)
    }
}

@Composable
fun TeamBadge(team: TeamUiModel, modifier: Modifier = Modifier) {
    Box(modifier.size(38.dp).background(team.color, CircleShape), contentAlignment = Alignment.Center) {
        Text(team.abbreviation, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
}
