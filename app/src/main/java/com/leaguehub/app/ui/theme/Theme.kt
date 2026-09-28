package com.leaguehub.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

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

    error = LeagueLive
)

@Composable
fun LeagueHubTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LeagueHubColorScheme,
        typography = LeagueHubTypography,
        shapes = LeagueHubShapes,
        content = content
    )
}