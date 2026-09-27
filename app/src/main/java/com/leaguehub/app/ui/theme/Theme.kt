package com.leaguehub.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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