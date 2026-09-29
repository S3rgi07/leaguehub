package com.leaguehub.app.core.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.feature.team.model.TeamUiModel

enum class PlayerDestination(val label: String, @param:DrawableRes val icon: Int) {
    HOME("Inicio", R.drawable.lh_home),
    STANDINGS("Tabla", R.drawable.lh_trophy),
    CALENDAR("Partidos", R.drawable.lh_calendar),
    PROFILE("Perfil", R.drawable.lh_user)
}
@Composable
fun PlayerScaffold(
    selected: PlayerDestination,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    Surface(modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.statusBarsPadding()) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                PlayerDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selected == destination,
                        onClick = { onNavigate(destination) },
                        icon = { LeagueIcon(destination.icon, null) },
                        label = { Text(destination.label, style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .14f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LeagueIcon(@DrawableRes icon: Int, description: String?, modifier: Modifier = Modifier, tint: Color = LocalContentColor.current) {
    Icon(painterResource(icon), description, modifier.size(24.dp), tint = tint)
}

@Composable
fun LeagueIconButton(@DrawableRes icon: Int, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick, modifier.size(48.dp)) {
        LeagueIcon(icon, label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PlayerTopBar(
    title: String,
    subtitle: String = "",
    onBack: (() -> Unit)? = null,
    action: @Composable RowScope.() -> Unit = {}
) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) LeagueIconButton(R.drawable.lh_back, "Volver", onBack)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (title.isNotEmpty()) Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
            if (subtitle.isNotEmpty()) MutedText(subtitle)
        }
        action()
    }
}

@Composable
fun MutedText(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun SectionHeading(title: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().heightIn(min = 36.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f).semantics { heading() }, style = MaterialTheme.typography.titleMedium)
        if (actionLabel != null) TextButton(onClick = onAction) {
            Text(actionLabel, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun LeagueBadge(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary, @DrawableRes icon: Int? = null) {
    Row(
        modifier.background(color.copy(alpha = .12f), MaterialTheme.shapes.small).padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) LeagueIcon(icon, null, Modifier.size(16.dp), color)
        Text(text, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun TeamBadge(team: TeamUiModel, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    Box(
        modifier.size(size).background(
            Brush.linearGradient(listOf(team.color, team.color.copy(alpha = .45f))), CircleShape
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            team.abbreviation,
            style = if (size >= 64.dp) MaterialTheme.typography.titleLarge else MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PlayerAvatar(name: String, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Image(painterResource(R.drawable.lh_avatar), "Avatar de $name", modifier.size(size))
}

@Composable
fun HeroCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxWidth().background(
            Brush.linearGradient(listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = .12f),
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.secondary.copy(alpha = .16f)
            )), MaterialTheme.shapes.large
        ).border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
fun StatCard(value: String, label: String, @DrawableRes icon: Int, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    LeagueHubCard(modifier.border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large), contentPadding = PaddingValues(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(value, Modifier.weight(1f), style = MaterialTheme.typography.headlineLarge, color = color, maxLines = 1)
            LeagueIcon(icon, null, Modifier.size(20.dp), MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(20.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ChoiceTabs(labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        labels.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                shape = SegmentedButtonDefaults.itemShape(index, labels.size),
                icon = {},
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    activeContentColor = MaterialTheme.colorScheme.primary,
                    inactiveContainerColor = MaterialTheme.colorScheme.surface,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    activeBorderColor = MaterialTheme.colorScheme.outline,
                    inactiveBorderColor = MaterialTheme.colorScheme.outline
                )
            ) { Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        }
    }
}

@Composable
fun EmptyContent(message: String) {
    LeagueHubCard(Modifier.fillMaxWidth()) { MutedText(message, Modifier.padding(vertical = 20.dp)) }
}

@Composable
fun SecondaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, @DrawableRes icon: Int? = null) {
    OutlinedButton(onClick, modifier.heightIn(min = 48.dp), shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)) {
        if (icon != null) {
            LeagueIcon(icon, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}
