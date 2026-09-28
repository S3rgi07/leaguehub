package com.leaguehub.app.feature.onboarding.league

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.ui.theme.LeagueHubTheme

data class LeagueOptionUiModel(
    val id: String,
    val name: String,
    val description: String,
    val abbreviation: String,
    val color: Color
)

@Composable
fun LeagueSelectionScreen(
    leagues: List<LeagueOptionUiModel>,
    popularLeagues: List<LeagueOptionUiModel>,
    selectedLeagueId: String?,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchChange: (String) -> Unit = {},
    onLeagueSelected: (String) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onExploreLaterClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp)
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

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "Encuentra tu liga",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Paso 2 de 2",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "BUSCAR",
            modifier = Modifier.padding(bottom = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Nombre, ciudad o código",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyMedium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CERCA DE TI",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "GUATEMALA",
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leagues.forEach { league ->
                LeagueOptionCard(
                    league = league,
                    selected = league.id == selectedLeagueId,
                    onClick = {
                        onLeagueSelected(league.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "POPULAR ESTA SEMANA",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(10.dp))

        popularLeagues.forEach { league ->
            LeagueOptionCard(
                league = league,
                selected = league.id == selectedLeagueId,
                onClick = {
                    onLeagueSelected(league.id)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        LeagueHubButton(
            text = "Seguir liga",
            onClick = onContinueClick,
            enabled = selectedLeagueId != null
        )

        Text(
            text = "Explorar más tarde",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 14.dp)
                .clickable { onExploreLaterClick() },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun LeagueOptionCard(
    league: LeagueOptionUiModel,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.medium
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = league.color
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = league.abbreviation,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = league.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = league.description,
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = if (selected) "✓" else "+",
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
    }
}

private val previewNearbyLeagues = listOf(
    LeagueOptionUiModel(
        id = "university",
        name = "Liga Universitaria GT",
        description = "12 equipos · Jornada 8",
        abbreviation = "LU",
        color = Color(0xFF76A942)
    ),
    LeagueOptionUiModel(
        id = "zona7",
        name = "Copa Zona 7",
        description = "8 equipos · Apertura",
        abbreviation = "C7",
        color = Color(0xFFE8752E)
    ),
    LeagueOptionUiModel(
        id = "amateur",
        name = "Amateur Metropolitana",
        description = "16 equipos · Clausura",
        abbreviation = "AM",
        color = Color(0xFF1687D4)
    )
)

private val previewPopularLeagues = listOf(
    LeagueOptionUiModel(
        id = "campus",
        name = "Liga Fútbol Campus",
        description = "10 equipos · Mixta",
        abbreviation = "FC",
        color = Color(0xFF1687D4)
    )
)

@Preview(
    name = "Vincular liga - Seleccionada",
    showBackground = true
)
@Composable
private fun LeagueSelectionPreview() {
    LeagueHubTheme {
        LeagueSelectionScreen(
            leagues = previewNearbyLeagues,
            popularLeagues = previewPopularLeagues,
            selectedLeagueId = "university"
        )
    }
}

