package com.leaguehub.app.feature.explore

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class ExploreCategory {
    ALL, LEAGUES, TEAMS, PLAYERS
}

data class ExploreItemUiModel(
    val id: String,
    val name: String,
    val description: String,
    val category: ExploreCategory,
    @DrawableRes val iconRes: Int,
    val badge: String? = null
)

@Composable
fun ExploreScreen(
    searchQuery: String,
    selectedCategory: ExploreCategory,
    featuredLeagues: List<ExploreItemUiModel>,
    popularTeams: List<ExploreItemUiModel>,
    suggestedPlayers: List<ExploreItemUiModel>,
    modifier: Modifier = Modifier,
    onSearchChange: (String) -> Unit = {},
    onCategoryChange: (ExploreCategory) -> Unit = {},
    onItemClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
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

                Column {
                    Text(
                        text = "Explorar",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Descubre tu comunidad deportiva",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                placeholder = {
                    Text(
                        text = "Buscar ligas, equipos o jugadores...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        item {
            ExploreFilters(
                selectedCategory = selectedCategory,
                onCategoryChange = onCategoryChange
            )
        }

        item {
            ExploreSectionHeader(
                title = "LIGAS DESTACADAS",
                action = "Ver todas"
            )
        }

        items(
            items = featuredLeagues,
            key = { "league_${it.id}" }
        ) { item ->
            ExploreResultCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }

        item {
            ExploreSectionHeader(
                title = "EQUIPOS POPULARES",
                action = "Ver todos"
            )
        }

        items(
            items = popularTeams,
            key = { "team_${it.id}" }
        ) { item ->
            ExploreResultCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }

        item {
            ExploreSectionHeader(
                title = "JUGADORES DESTACADOS",
                action = "Ver todos"
            )
        }

        items(
            items = suggestedPlayers,
            key = { "player_${it.id}" }
        ) { item ->
            ExploreResultCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }
    }
}

@Composable
private fun ExploreFilters(
    selectedCategory: ExploreCategory,
    onCategoryChange: (ExploreCategory) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val categories = listOf(
            ExploreCategory.ALL to "Todo",
            ExploreCategory.LEAGUES to "Ligas",
            ExploreCategory.TEAMS to "Equipos",
            ExploreCategory.PLAYERS to "Jugadores"
        )

        categories.forEach { (category, label) ->
            val selected = category == selectedCategory

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCategoryChange(category) },
                shape = MaterialTheme.shapes.medium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 10.dp
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
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
private fun ExploreSectionHeader(
    title: String,
    action: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = action,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ExploreResultCard(
    item: ExploreItemUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.category == ExploreCategory.PLAYERS) {
                    Image(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (item.badge != null) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Icon(
                painter = painterResource(R.drawable.lh_chevron),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val previewLeagues = listOf(
    ExploreItemUiModel(
        id = "university",
        name = "Liga Universitaria GT",
        description = "12 equipos · Jornada 8",
        category = ExploreCategory.LEAGUES,
        iconRes = R.drawable.lh_league,
        badge = "EN CURSO"
    ),
    ExploreItemUiModel(
        id = "metropolitana",
        name = "Liga Metropolitana",
        description = "16 equipos · Apertura 2026",
        category = ExploreCategory.LEAGUES,
        iconRes = R.drawable.lh_league
    )
)

private val previewTeams = listOf(
    ExploreItemUiModel(
        id = "falcons",
        name = "Falcons",
        description = "Liga Universitaria GT · 2.º lugar",
        category = ExploreCategory.TEAMS,
        iconRes = R.drawable.lh_shield
    ),
    ExploreItemUiModel(
        id = "titans",
        name = "Titans",
        description = "Liga Universitaria GT · 3.º lugar",
        category = ExploreCategory.TEAMS,
        iconRes = R.drawable.lh_shield
    )
)

private val previewPlayers = listOf(
    ExploreItemUiModel(
        id = "sofia",
        name = "Sofía Méndez",
        description = "Falcons · Delantera · #10",
        category = ExploreCategory.PLAYERS,
        iconRes = R.drawable.lh_avatar,
        badge = "12 GOLES"
    )
)

@Preview(
    name = "P14 - Explorar",
    showBackground = true
)
@Composable
private fun ExploreScreenPreview() {
    LeagueHubTheme {
        ExploreScreen(
            searchQuery = "",
            selectedCategory = ExploreCategory.ALL,
            featuredLeagues = previewLeagues,
            popularTeams = previewTeams,
            suggestedPlayers = previewPlayers
        )
    }
}

