package com.leaguehub.app.feature.onboarding.welcome.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingHero(
    modifier: Modifier = Modifier
) {
    val fieldColor = Color(0xFF123B25)
    val fieldLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)

    val infoColor = MaterialTheme.colorScheme.secondary
    val primaryColor = MaterialTheme.colorScheme.primary
    val manageColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            )
            .padding(20.dp)
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val borderWidth = 1.dp.toPx()
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            // Fondo de la cancha
            drawRoundRect(
                color = fieldColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    6.dp.toPx()
                )
            )

            // Borde de la cancha
            drawRoundRect(
                color = fieldLineColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    6.dp.toPx()
                ),
                style = Stroke(width = borderWidth)
            )

            // Línea central punteada
            drawLine(
                color = fieldLineColor,
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = borderWidth,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(
                        8.dp.toPx(),
                        8.dp.toPx()
                    )
                )
            )

            // Círculo central
            drawCircle(
                color = fieldLineColor,
                radius = 38.dp.toPx(),
                center = Offset(centerX, centerY),
                style = Stroke(width = borderWidth)
            )

            // Punto central
            drawCircle(
                color = fieldLineColor,
                radius = 2.dp.toPx(),
                center = Offset(centerX, centerY)
            )

            // Jugador azul
            drawCircle(
                color = infoColor,
                radius = 10.dp.toPx(),
                center = Offset(
                    x = size.width * 0.28f,
                    y = size.height * 0.34f
                )
            )

            // Jugador verde
            drawCircle(
                color = primaryColor,
                radius = 8.dp.toPx(),
                center = Offset(
                    x = size.width * 0.42f,
                    y = size.height * 0.72f
                )
            )

            // Jugador naranja
            drawCircle(
                color = manageColor,
                radius = 11.dp.toPx(),
                center = Offset(
                    x = size.width * 0.73f,
                    y = size.height * 0.57f
                )
            )

            // Balón simplificado
            drawCircle(
                color = Color.Black,
                radius = 7.dp.toPx(),
                center = Offset(centerX, centerY),
                style = Stroke(
                    width = 1.5.dp.toPx()
                )
            )

            drawCircle(
                color = Color.Black,
                radius = 2.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }
    }
}