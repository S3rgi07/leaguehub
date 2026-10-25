package com.leaguehub.app.feature.organizer.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun PublishedResultScreen(
    homeTeam: String,
    awayTeam: String,
    homeScore: Int,
    awayScore: Int,
    matchday: Int,
    venue: String,
    publishedTime: String,
    viewerCount: Int,
    updatedPlayers: Int,
    modifier: Modifier = Modifier,
    onMatchCenterClick: () -> Unit = {},
    onBackToDashboardClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(18.dp))

        // Confirmación visual
        SuccessIllustration()

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "¡Resultado publicado!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "La jornada y las estadísticas ya están al día.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Marcador final
        FinalResultCard(
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeScore = homeScore,
            awayScore = awayScore,
            matchday = matchday,
            venue = venue,
            publishedTime = publishedTime
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SINCRONIZACIÓN",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        SyncStatusItem(
            title = "Resultado y cronología",
            description = "Visible para $viewerCount seguidores"
        )

        Spacer(modifier = Modifier.height(18.dp))

        SyncStatusItem(
            title = "Tabla de posiciones",
            description = "Falcons sube al 2.º lugar"
        )

        Spacer(modifier = Modifier.height(18.dp))

        SyncStatusItem(
            title = "Estadísticas de jugadores",
            description = "$updatedPlayers perfiles actualizados"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onMatchCenterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF35DF9F),
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                text = "◉ Match Center",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBackToDashboardClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Text(
                text = "Volver al panel",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Todo guardado · Puedes cerrar con seguridad",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SuccessIllustration() {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val success = Color(0xFF35DF9F)

    Box(
        modifier = Modifier.size(132.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                color = success.copy(alpha = 0.08f),
                radius = size.minDimension * 0.46f,
                center = center
            )

            drawCircle(
                color = success.copy(alpha = 0.12f),
                radius = size.minDimension * 0.31f,
                center = center
            )

            drawCircle(
                color = success,
                radius = size.minDimension * 0.31f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = primary,
                radius = 3.dp.toPx(),
                center = Offset(
                    size.width * 0.28f,
                    size.height * 0.16f
                )
            )

            drawCircle(
                color = secondary,
                radius = 2.5.dp.toPx(),
                center = Offset(
                    size.width * 0.82f,
                    size.height * 0.27f
                )
            )

            drawCircle(
                color = tertiary,
                radius = 4.dp.toPx(),
                center = Offset(
                    size.width * 0.90f,
                    size.height * 0.72f
                )
            )

            drawCircle(
                color = Color(0xFFAA80FF),
                radius = 2.5.dp.toPx(),
                center = Offset(
                    size.width * 0.17f,
                    size.height * 0.82f
                )
            )
        }

        Text(
            text = "✓",
            style = MaterialTheme.typography.headlineLarge,
            color = success
        )
    }
}

@Composable
private fun FinalResultCard(
    homeTeam: String,
    awayTeam: String,
    homeScore: Int,
    awayScore: Int,
    matchday: Int,
    venue: String,
    publishedTime: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFF35DF9F).copy(alpha = 0.12f)
            ) {
                Text(
                    text = "✓ FINAL OFICIAL",
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF35DF9F)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamBadge("FAL", MaterialTheme.colorScheme.secondary)

                Text(
                    text = homeTeam,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "$homeScore – $awayScore",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = awayTeam,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TeamBadge("TIT", MaterialTheme.colorScheme.tertiary)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jornada $matchday · $venue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Publicado hoy · $publishedTime",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF35DF9F)
            )
        }
    }
}

@Composable
private fun TeamBadge(
    abbreviation: String,
    color: Color
) {
    Surface(
        modifier = Modifier.size(42.dp),
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

@Composable
private fun SyncStatusItem(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CircleShape,
            color = Color(0xFF35DF9F).copy(alpha = 0.15f),
            border = BorderStroke(
                1.dp,
                Color(0xFF35DF9F)
            )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF35DF9F)
                )
            }
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(
    name = "O07 - Resultado publicado",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun PublishedResultPreview() {
    LeagueHubTheme {
        PublishedResultScreen(
            homeTeam = "Falcons",
            awayTeam = "Titans",
            homeScore = 2,
            awayScore = 1,
            matchday = 8,
            venue = "Cancha Central",
            publishedTime = "21:18",
            viewerCount = 1248,
            updatedPlayers = 6
        )
    }
}
