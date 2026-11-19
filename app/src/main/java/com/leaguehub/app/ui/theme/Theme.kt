package com.leaguehub.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable

private val LeagueHubColorScheme = darkColorScheme(
    primary = LeaguePrimary,
    onPrimary = LeagueOnPrimary,

    secondary = LeagueInfo,
    onSecondary = LeagueBackground,

    tertiary = LeagueManage,
    onTertiary = LeagueBackground,

    background = LeagueBackground,
    onBackground = LeagueOnBackground,

    surface = LeagueSurface,
    onSurface = LeagueOnSurface,

    error = LeagueLive,
    onSurfaceVariant = LeagueMuted,
    outline = LeagueOutline,
    outlineVariant = LeagueOutline,
    surfaceVariant = LeagueRaised,
    surfaceContainer = LeagueSurface,
    surfaceContainerHigh = LeagueRaised,
    secondaryContainer = LeagueRaised,
    onSecondaryContainer = LeagueInfo,
    tertiaryContainer = LeagueInsightSurface,
    onTertiaryContainer = LeagueInsight
)

@Composable
fun LeagueHubTheme(content: @Composable () -> Unit) {
    LeagueHubTheme(darkTheme = true, content = content)
}

@Composable
fun LeagueHubTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) LeagueHubColorScheme else lightColorScheme(),
        typography = LeagueHubTypography,
        shapes = LeagueHubShapes,
        content = content
    )
}
