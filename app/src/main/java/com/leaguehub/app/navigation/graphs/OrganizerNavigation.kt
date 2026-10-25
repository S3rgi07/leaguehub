package com.leaguehub.app.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.leaguehub.app.feature.organizer.dashboard.OrganizerDashboardRoute
import com.leaguehub.app.navigation.OrganizerDashboardDestination

fun NavGraphBuilder.organizerNavigation() {
    composable<OrganizerDashboardDestination> {
        OrganizerDashboardRoute()
    }
}