package com.leaguehub.app.feature.organizer.validation.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.MatchScoreboard
import com.leaguehub.app.data.fake.falcons
import com.leaguehub.app.data.fake.titans
import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class DetectedEventType {
    GOAL, YELLOW_CARD, SUBSTITUTION
}

data class DetectedEventUiModel(
    val id: String,
    val minute: Int,
    val title: String,
    val description: String,
    val type: DetectedEventType,
    val needsReview: Boolean = false
)

@Composable
fun EventValidationScreen(
    events: List<DetectedEventUiModel>,
    modifier: Modifier = Modifier,
    homeScore: Int = 2,
    awayScore: Int = 1,
    lineupCount: Int = 22,
    onBackClick: () -> Unit = {},
    onEventEditClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {},
    onCorrectDataClick: () -> Unit = {},
    onPublishClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(end = 16.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Validar partido",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Paso 3 de 3 · Falcons 2–1 Titans",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.size(36.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "☰",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Marcador
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamCircle("FAL", MaterialTheme.colorScheme.secondary)

                    Text(
                        text = "Falcons",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "$homeScore – $awayScore",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Titans",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TeamCircle("TIT", MaterialTheme.colorScheme.tertiary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "✓ FINAL",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pestañas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    MaterialTheme.shapes.medium
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ValidationTab(
                text = "Cronología · ${events.size}",
                selected = true,
                modifier = Modifier.weight(1f)
            )

            ValidationTab(
                text = "Alineaciones · $lineupCount",
                selected = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EVENTOS DETECTADOS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lista de eventos con scroll
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = events,
                key = { _, event -> event.id }
            ) { _, event ->
                DetectedEventRow(
                    event = event,
                    onEditClick = { onEventEditClick(event.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                ) {
                    Text(
                        text = "ⓘ  Confirma la asistencia del gol al 31'",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Acciones secundarias
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onAddEventClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Agregar evento",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onCorrectDataClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Corregir dato",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onPublishClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text(
                text = "Publicar resultado",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ValidationTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            Color.Transparent
        }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun DetectedEventRow(
    event: DetectedEventUiModel,
    onEditClick: () -> Unit
) {
    val accent = if (event.needsReview) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            border = BorderStroke(
                1.dp,
                accent.copy(alpha = 0.45f)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = when (event.type) {
                        DetectedEventType.GOAL -> "⚽"
                        DetectedEventType.YELLOW_CARD -> "▣"
                        DetectedEventType.SUBSTITUTION -> "↔"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = accent
                )
            }
        }

        Surface(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium,
            color = if (event.needsReview) {
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f)
            } else {
                MaterialTheme.colorScheme.surface
            },
            border = BorderStroke(
                1.dp,
                if (event.needsReview) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.minute}'",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (event.needsReview) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "✎",
                    modifier = Modifier
                        .clickable { onEditClick() }
                        .padding(4.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TeamCircle(
    abbreviation: String,
    color: Color
) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = CircleShape,
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = abbreviation,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

val previewEvents = listOf(
    DetectedEventUiModel(
        id = "1",
        minute = 75,
        title = "Tarjeta amarilla",
        description = "D. Reyes · Titans",
        type = DetectedEventType.YELLOW_CARD
    ),
    DetectedEventUiModel(
        id = "2",
        minute = 68,
        title = "Gol · Falcons",
        description = "S. Méndez · asistencia A. López",
        type = DetectedEventType.GOAL
    ),
    DetectedEventUiModel(
        id = "3",
        minute = 61,
        title = "Sustitución · Titans",
        description = "Entra J. Castillo · sale M. León",
        type = DetectedEventType.SUBSTITUTION
    ),
    DetectedEventUiModel(
        id = "4",
        minute = 43,
        title = "Gol · Falcons",
        description = "A. López · jugada individual",
        type = DetectedEventType.GOAL
    ),
    DetectedEventUiModel(
        id = "5",
        minute = 31,
        title = "Gol · Titans",
        description = "M. León · asistencia por revisar",
        type = DetectedEventType.GOAL,
        needsReview = true
    )
)

@Preview(
    name = "O05 - Validar partido",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun EventValidationPreview() {
    LeagueHubTheme {
        EventValidationScreen(events = previewEvents)
    }
}
