package com.leaguehub.app.feature.organizer.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.ui.theme.LeagueHubTheme

data class LiveEventUiModel(
    val id: String,
    val minute: String,
    val title: String,
    val description: String
)

@Composable
fun LiveControlScreen(
    homeScore: Int,
    awayScore: Int,
    matchTime: String,
    recentEvents: List<LiveEventUiModel>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onCardClick: () -> Unit = {},
    onSubstitutionClick: () -> Unit = {},
    onPauseClick: () -> Unit = {},
    onEventEditClick: (String) -> Unit = {},
    onFinishMatchClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {

        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    text = "Control en vivo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Liga Universitaria · Jornada 8",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onShareClick() },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "↗",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Marcador en vivo
        LiveScoreCard(
            homeScore = homeScore,
            awayScore = awayScore,
            matchTime = matchTime,
            onPauseClick = onPauseClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "REGISTRAR ACCIÓN",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Acciones principales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiveActionButton(
                text = "Gol",
                symbol = "⚽",
                accent = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = onGoalClick
            )

            LiveActionButton(
                text = "Tarjeta",
                symbol = "▣",
                accent = Color(0xFFFFC928),
                modifier = Modifier.weight(1f),
                onClick = onCardClick
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LiveActionButton(
                text = "Sustitución",
                symbol = "↔",
                accent = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = onSubstitutionClick
            )

            LiveActionButton(
                text = "Pausar",
                symbol = "◷",
                accent = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f),
                onClick = onPauseClick
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Últimos eventos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "${recentEvents.size} EVENTOS",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recentEvents.take(2).forEach { event ->
                LiveEventRow(
                    event = event,
                    onEditClick = { onEventEditClick(event.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Estado de sincronización
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◉",
                    color = MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "Vista pública actualizada hace 3 s",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Finalizar partido
        OutlinedButton(
            onClick = onFinishMatchClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.error
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "⚑  Finalizar partido",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LiveScoreCard(
    homeScore: Int,
    awayScore: Int,
    matchTime: String,
    onPauseClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            Color(0xFF35DF9F)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "◉ EN VIVO",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF35DF9F)
                )

                Text(
                    text = "SINCRONIZADO",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF35DF9F)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamCircle("FAL", MaterialTheme.colorScheme.secondary)

                Text(
                    text = "Falcons",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$homeScore – $awayScore",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Titans",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TeamCircle("TIT", MaterialTheme.colorScheme.tertiary)
            }

            Text(
                text = matchTime,
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF35DF9F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.clickable { onPauseClick() },
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
            ) {
                Text(
                    text = "◷ Pausar reloj",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LiveActionButton(
    text: String,
    symbol: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(54.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = accent.copy(alpha = 0.13f),
        border = BorderStroke(1.dp, accent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.titleMedium,
                color = accent
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "+",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LiveEventRow(
    event: LiveEventUiModel,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.minute,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
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

@Composable
private fun TeamCircle(
    abbreviation: String,
    color: Color
) {
    Surface(
        modifier = Modifier.size(40.dp),
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

private val previewEvents = listOf(
    LiveEventUiModel(
        id = "1",
        minute = "68'",
        title = "Gol de S. Méndez",
        description = "Falcons · 2–1"
    ),
    LiveEventUiModel(
        id = "2",
        minute = "61'",
        title = "Entra J. Castillo",
        description = "Titans · Sustitución"
    )
)

@Preview(
    name = "O06 - Control en vivo",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LiveControlPreview() {
    LeagueHubTheme {
        LiveControlScreen(
            homeScore = 2,
            awayScore = 1,
            matchTime = "75:24",
            recentEvents = previewEvents
        )
    }
}
