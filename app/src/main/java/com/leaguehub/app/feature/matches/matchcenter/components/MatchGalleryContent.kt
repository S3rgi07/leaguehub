package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun MatchGalleryContent(
    modifier: Modifier = Modifier,
    onSharePhotoClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Galería de la comunidad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "24 FOTOS",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Primera fila: foto grande + dos pequeñas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(174.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(2f).fillMaxHeight()) {
                GalleryPhoto(
                    background = Color(0xFF173D56),
                    hill = Color(0xFF071D2D),
                    sun = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "DESTACADA",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GalleryPhoto(
                    background = Color(0xFF382619),
                    hill = Color(0xFF1A1713),
                    sun = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                GalleryPhoto(
                    background = Color(0xFF1F4025),
                    hill = Color(0xFF102618),
                    sun = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        // Segunda fila: tres fotografías
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(94.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GalleryPhoto(
                background = Color(0xFF39243D),
                hill = Color(0xFF201526),
                sun = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            GalleryPhoto(
                background = Color(0xFF51202C),
                hill = Color(0xFF2A1019),
                sun = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            GalleryPhoto(
                background = Color(0xFF163C52),
                hill = Color(0xFF0A2333),
                sun = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // Compartir fotografía
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSharePhotoClick() },
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "↑",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comparte tu mejor toma",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "JPG o PNG · máx. 10 MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "›",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Al subir confirmas que tienes permiso para compartirla.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun GalleryPhoto(
    background: Color,
    hill: Color,
    sun: Color,
    modifier: Modifier = Modifier
) {
    val shape = MaterialTheme.shapes.medium

    Box(
        modifier = modifier
            .background(background, shape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val landscape = Path().apply {
                moveTo(0f, h * 0.85f)
                cubicTo(
                    w * 0.25f, h * 0.35f,
                    w * 0.38f, h * 0.75f,
                    w * 0.55f, h * 0.85f
                )
                cubicTo(
                    w * 0.75f, h * 1.05f,
                    w * 0.90f, h * 0.65f,
                    w, h * 0.58f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }

            drawPath(
                path = landscape,
                color = hill
            )

            drawCircle(
                color = sun.copy(alpha = 0.85f),
                radius = minOf(w, h) * 0.13f,
                center = Offset(
                    x = w * 0.72f,
                    y = h * 0.32f
                )
            )
        }
    }
}

@Preview(
    name = "Galería del partido",
    showBackground = true
)
@Composable
private fun MatchGalleryPreview() {
    LeagueHubTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            MatchGalleryContent()
        }
    }
}
