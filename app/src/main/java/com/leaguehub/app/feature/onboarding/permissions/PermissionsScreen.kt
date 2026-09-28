package com.leaguehub.app.feature.onboarding.permissions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import com.leaguehub.app.R
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun PermissionsScreen(
    liveMatchesEnabled: Boolean,
    remindersEnabled: Boolean,
    smartPreviewEnabled: Boolean,
    locationEnabled: Boolean,
    modifier: Modifier = Modifier,
    onLiveMatchesChange: (Boolean) -> Unit = {},
    onRemindersChange: (Boolean) -> Unit = {},
    onSmartPreviewChange: (Boolean) -> Unit = {},
    onLocationChange: (Boolean) -> Unit = {},
    onEnterLeagueHubClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NotificationHero()

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "No te pierdas el juego",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Elige qué alertas quieres recibir.",
            modifier = Modifier.padding(top = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PermissionOption(
                title = "Partidos en vivo",
                description = "Goles, tarjetas y final",
                checked = liveMatchesEnabled,
                onCheckedChange = onLiveMatchesChange
            )

            PermissionOption(
                title = "Recordatorios",
                description = "30 min antes de cada partido",
                checked = remindersEnabled,
                onCheckedChange = onRemindersChange
            )

            PermissionOption(
                title = "Previa inteligente",
                description = "Pronóstico y datos destacados",
                checked = smartPreviewEnabled,
                onCheckedChange = onSmartPreviewChange
            )

            PermissionOption(
                title = "Ubicación",
                description = "Mapas y canchas cercanas",
                checked = locationEnabled,
                onCheckedChange = onLocationChange
            )
        }

        Text(
            text = "Puedes ajustar todo desde Configuración.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.weight(1f))

        LeagueHubButton(
            text = "Entrar a LeagueHub",
            onClick = onEnterLeagueHubClick
        )

        Text(
            text = "Ahora no",
            modifier = Modifier.padding(top = 14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PermissionOption(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor =
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    uncheckedTrackColor =
                        MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun NotificationHero() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_notifications
                        ),
                        contentDescription = "Notificaciones",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Permisos y alertas",
    showBackground = true
)
@Composable
private fun PermissionsScreenPreview() {
    LeagueHubTheme {
        PermissionsScreen(
            liveMatchesEnabled = true,
            remindersEnabled = true,
            smartPreviewEnabled = true,
            locationEnabled = false
        )
    }
}