package com.leaguehub.app.feature.organizer.validation.ocr

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.ui.theme.LeagueHubTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset

data class OcrReviewUiModel(
    val homeTeam: String,
    val awayTeam: String,
    val dateTime: String,
    val referee: String,
    val homeScore: Int,
    val awayScore: Int,
    val confidence: Int,
    val detectedFields: Int
)

@Composable
fun OcrReviewScreen(
    data: OcrReviewUiModel,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditRefereeClick: () -> Unit = {},
    onEditScoreClick: () -> Unit = {},
    onValidateClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                modifier = Modifier.clickable { onBackClick() },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "Revisar datos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Paso 2 de 3 · Lectura OCR",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        OcrProgressTabs()

        OcrConfidenceCard(
            confidence = data.confidence,
            detectedFields = data.detectedFields
        )

        Text(
            text = "DATOS RECONOCIDOS",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OcrDataField(
            label = "EQUIPO LOCAL",
            value = data.homeTeam
        )

        OcrDataField(
            label = "EQUIPO VISITANTE",
            value = data.awayTeam
        )

        OcrDataField(
            label = "FECHA · HORA",
            value = data.dateTime
        )

        OcrDataField(
            label = "ÁRBITRO",
            value = data.referee,
            needsReview = true,
            onClick = onEditRefereeClick
        )

        Text(
            text = "MARCADOR FINAL",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OcrScoreCard(
            homeScore = data.homeScore,
            awayScore = data.awayScore,
            onEditClick = onEditScoreClick
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(
                text = "ⓘ  Revisa 1 campo resaltado antes de continuar",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onValidateClick,
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
                text = "Validar cronología",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun OcrProgressTabs() {
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
        listOf("Captura", "Revisión", "Validación").forEach { tab ->
            val selected = tab == "Revisión"

            Surface(
                modifier = Modifier.weight(1f),
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
                        text = tab,
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
    }
}

@Composable
private fun OcrConfidenceCard(
    confidence: Int,
    detectedFields: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▤",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LECTURA COMPLETADA",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF35DF9F)
                )

                Text(
                    text = "$confidence%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { confidence / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF35DF9F),
                    trackColor = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = "$detectedFields campos detectados",
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OcrDataField(
    label: String,
    value: String,
    needsReview: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (needsReview) {
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            1.dp,
            if (needsReview) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }
        )
    ) {
        Row(
            modifier = Modifier.clickable(onClick = onClick).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (needsReview) "✎" else "✓",
                style = MaterialTheme.typography.titleMedium,
                color = if (needsReview) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun OcrScoreCard(
    homeScore: Int,
    awayScore: Int,
    onEditClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamScoreCircle(
                    "FAL",
                    MaterialTheme.colorScheme.secondary
                )

                Text(
                    text = "$homeScore",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "–",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "$awayScore",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                TeamScoreCircle(
                    "TIT",
                    MaterialTheme.colorScheme.tertiary
                )
            }

            // Icono para editar el marcador
            Text(
                text = "✎",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 12.dp)
                    .clickable { onEditClick() }
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TeamScoreCircle(
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

@Preview(
    name = "O04 - Revisar datos OCR",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun OcrReviewPreview() {
    LeagueHubTheme {
        OcrReviewScreen(
            data = OcrReviewUiModel(
                homeTeam = "Falcons FC",
                awayTeam = "Titans",
                dateTime = "06 sep 2026 · 19:30",
                referee = "Marco Salazar",
                homeScore = 2,
                awayScore = 1,
                confidence = 94,
                detectedFields = 12
            )
        )
    }
}
