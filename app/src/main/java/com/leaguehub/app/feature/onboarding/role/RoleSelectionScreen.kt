package com.leaguehub.app.feature.onboarding.role

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class UserRole {
    PLAYER,
    ORGANIZER
}

@Composable
fun RoleSelectionScreen(
    selectedRole: UserRole,
    modifier: Modifier = Modifier,
    onRoleSelected: (UserRole) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onConfigureLeagueClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {

        Text(
            text = "‹",
            modifier = Modifier.clickable { onBackClick() },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Elige tu experiencia",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Puedes cambiarla después",
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Cómo usarás",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "LeagueHub?",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Personalizamos las herramientas para tu día a día.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        RoleCard(
            title = "Jugador o aficionado",
            description = "Calendario, tabla, live center\nSeguir partidos y rendimiento",
            badge = "EXPERIENCIA FAN",
            selected = selectedRole == UserRole.PLAYER,
            onClick = {
                onRoleSelected(UserRole.PLAYER)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        RoleCard(
            title = "Organizador",
            description = "Ingreso rápido y control total\nGestionar equipos y resultados",
            badge = "MODO GESTIÓN",
            selected = selectedRole == UserRole.ORGANIZER,
            organizer = true,
            onClick = {
                onRoleSelected(UserRole.ORGANIZER)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        InfoCard()

        Spacer(modifier = Modifier.weight(1f))

        LeagueHubButton(
            text = if (selectedRole == UserRole.PLAYER) {
                "Continuar como jugador"
            } else {
                "Continuar como organizador"
            },
            onClick = onContinueClick
        )

        Text(
            text = "Configurar una liga",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 14.dp)
                .clickable { onConfigureLeagueClick() },
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
@Composable
private fun RoleCard(
    title: String,
    description: String,
    badge: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    organizer: Boolean = false,
    onClick: () -> Unit
) {
    val accentColor = if (organizer) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }

    val borderColor = if (selected) {
        accentColor
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.large
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = if (selected) {
            accentColor.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = description,
                        modifier = Modifier.padding(top = 6.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                    )
                }

                SelectionIndicator(
                    selected = selected,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = badge,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor
            )
        }
    }
}

@Composable
private fun SelectionIndicator(
    selected: Boolean,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (selected) {
                    color
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                shape = CircleShape
            ),
        color = if (selected) {
            color
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        if (selected) {
            Surface(
                modifier = Modifier.padding(6.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary
            ) {}
        }
    }
}

@Composable
private fun InfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Un perfil, dos modos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Cambia de vista sin cerrar sesión.",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(
    name = "Elegir rol - Jugador",
    showBackground = true
)
@Composable
private fun RoleSelectionPlayerPreview() {
    LeagueHubTheme {
        RoleSelectionScreen(
            selectedRole = UserRole.PLAYER
        )
    }
}

@Preview(
    name = "Elegir rol - Organizador",
    showBackground = true
)
@Composable
private fun RoleSelectionOrganizerPreview() {
    LeagueHubTheme {
        RoleSelectionScreen(
            selectedRole = UserRole.ORGANIZER
        )
    }
}

