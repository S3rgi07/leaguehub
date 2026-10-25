package com.leaguehub.app.feature.notifications

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.ui.theme.LeagueHubTheme

enum class NotificationCategory {
    ALL, MATCHES, LEAGUE
}

enum class NotificationKind {
    GOAL, REMINDER, STANDINGS, PREDICTION, CALLUP, GALLERY
}

data class NotificationUiModel(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val kind: NotificationKind,
    val isRead: Boolean = false
)

@Composable
fun NotificationsScreen(
    todayNotifications: List<NotificationUiModel>,
    previousNotifications: List<NotificationUiModel>,
    modifier: Modifier = Modifier,
    selectedCategory: NotificationCategory = NotificationCategory.ALL,
    onCategoryChange: (NotificationCategory) -> Unit = {},
    onNotificationClick: (String) -> Unit = {},
    onMarkAllReadClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Notificaciones",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "4 nuevas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }

            Surface(
                modifier = Modifier.size(36.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "☷",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        NotificationTabs(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel("HOY")

                    Text(
                        text = "Marcar leídas",
                        modifier = Modifier.clickable {
                            onMarkAllReadClick()
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            items(
                items = todayNotifications,
                key = { it.id }
            ) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        onNotificationClick(notification.id)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionLabel("ANTERIORES")
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(
                items = previousNotifications,
                key = { it.id }
            ) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        onNotificationClick(notification.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationTabs(
    selectedCategory: NotificationCategory,
    onCategoryChange: (NotificationCategory) -> Unit
) {
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
        val tabs = listOf(
            NotificationCategory.ALL to "Todas",
            NotificationCategory.MATCHES to "Partidos",
            NotificationCategory.LEAGUE to "Liga"
        )

        tabs.forEach { (category, label) ->
            val selected = selectedCategory == category

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onCategoryChange(category)
                    },
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
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: NotificationUiModel,
    onClick: () -> Unit
) {
    val accentColor = when (notification.kind) {
        NotificationKind.GOAL -> MaterialTheme.colorScheme.primary
        NotificationKind.REMINDER -> MaterialTheme.colorScheme.secondary
        NotificationKind.STANDINGS -> MaterialTheme.colorScheme.tertiary
        NotificationKind.PREDICTION -> Color(0xFFAA80FF)
        NotificationKind.CALLUP -> MaterialTheme.colorScheme.onSurfaceVariant
        NotificationKind.GALLERY -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconRes = when (notification.kind) {
        NotificationKind.GOAL -> R.drawable.lh_radio
        NotificationKind.REMINDER -> R.drawable.lh_calendar
        NotificationKind.STANDINGS -> R.drawable.lh_trophy
        NotificationKind.PREDICTION -> R.drawable.lh_spark
        NotificationKind.CALLUP -> R.drawable.lh_users
        NotificationKind.GALLERY -> R.drawable.lh_news
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                )
            }

            if (!notification.isRead) {
                Surface(
                    modifier = Modifier
                        .size(7.dp)
                        .align(Alignment.Top),
                    shape = CircleShape,
                    color = accentColor
                ) {}
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

val previewToday = listOf(
    NotificationUiModel(
        "1",
        "¡Gol de Falcons!",
        "Sofía Méndez marcó el 2–1",
        "Ahora · Min 62'",
        NotificationKind.GOAL
    ),
    NotificationUiModel(
        "2",
        "Tu partido es en 3 días",
        "Falcons vs Titans · Cancha Central",
        "Hace 18 min",
        NotificationKind.REMINDER
    ),
    NotificationUiModel(
        "3",
        "Subiste al 2º lugar",
        "Falcons suma 19 puntos en la tabla",
        "Hace 2 h",
        NotificationKind.STANDINGS
    ),
    NotificationUiModel(
        "4",
        "Previa inteligente lista",
        "Consulta claves y pronóstico del domingo",
        "Hace 4 h",
        NotificationKind.PREDICTION
    )
)

val previewPrevious = listOf(
    NotificationUiModel(
        "5",
        "Nueva convocatoria",
        "R. Silva publicó la lista vs Titans",
        "Ayer · 19:32",
        NotificationKind.CALLUP,
        isRead = true
    ),
    NotificationUiModel(
        "6",
        "12 fotos nuevas",
        "La comunidad actualizó la galería",
        "Lun · 21:05",
        NotificationKind.GALLERY,
        isRead = true
    )
)

@Preview(
    name = "P12 - Notificaciones",
    showBackground = true
)
@Composable
private fun NotificationsScreenPreview() {
    LeagueHubTheme {
        NotificationsScreen(
            todayNotifications = previewToday,
            previousNotifications = previewPrevious
        )
    }
}
