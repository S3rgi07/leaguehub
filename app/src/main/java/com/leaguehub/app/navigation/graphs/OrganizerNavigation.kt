package com.leaguehub.app.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.leaguehub.app.feature.organizer.components.OrganizerSection
import com.leaguehub.app.feature.organizer.dashboard.OrganizerDashboardRoute
import com.leaguehub.app.feature.organizer.live.LiveControlRoute
import com.leaguehub.app.feature.organizer.matchmanagement.MatchManagementRoute
import com.leaguehub.app.feature.organizer.result.PublishedResultRoute
import com.leaguehub.app.feature.organizer.scan.ScanReportRoute
import com.leaguehub.app.feature.organizer.validation.events.EventValidationRoute
import com.leaguehub.app.feature.organizer.validation.ocr.OcrReviewRoute
import com.leaguehub.app.navigation.*

fun NavGraphBuilder.organizerNavigation(
    navController: NavHostController,
    onShare: (String) -> Unit
) {
    val defaultMatchId = "falcons_titans"
    val onSectionSelected: (OrganizerSection) -> Unit = { section ->
        when (section) {
            OrganizerSection.DASHBOARD -> navController.navigate(OrganizerDashboardDestination)
            OrganizerSection.SCAN -> navController.navigate(ScanReportDestination(defaultMatchId))
            OrganizerSection.MATCHES -> navController.navigate(MatchManagementDestination(defaultMatchId))
            OrganizerSection.SETTINGS -> navController.navigate(OrganizerDashboardDestination)
        }
    }

    composable<OrganizerDashboardDestination> {
        OrganizerDashboardRoute(
            onOpenControl = { navController.navigate(LiveControlDestination(it)) },
            onScan = { navController.navigate(ScanReportDestination(it)) },
            onManageMatch = { navController.navigate(MatchManagementDestination(it)) },
            onSectionSelected = onSectionSelected
        )
    }

    composable<MatchManagementDestination> { entry ->
        val destination = entry.toRoute<MatchManagementDestination>()
        MatchManagementRoute(
            matchId = destination.matchId,
            onStartLive = { navController.navigate(LiveControlDestination(it)) },
            onScan = { navController.navigate(ScanReportDestination(it)) },
            onSectionSelected = onSectionSelected
        )
    }

    composable<ScanReportDestination> { entry ->
        val destination = entry.toRoute<ScanReportDestination>()
        ScanReportRoute(
            matchId = destination.matchId,
            onProcessed = { navController.navigate(OcrReviewDestination(it)) },
            onSectionSelected = onSectionSelected
        )
    }

    composable<OcrReviewDestination> { entry ->
        val destination = entry.toRoute<OcrReviewDestination>()
        OcrReviewRoute(
            matchId = destination.matchId,
            onBack = { navController.popBackStack() },
            onValidated = { navController.navigate(EventValidationDestination(it)) }
        )
    }

    composable<EventValidationDestination> { entry ->
        val destination = entry.toRoute<EventValidationDestination>()
        EventValidationRoute(
            matchId = destination.matchId,
            onBack = { navController.popBackStack() },
            onCorrectData = { navController.popBackStack() },
            onPublished = { navController.navigate(PublishedResultDestination(it)) }
        )
    }

    composable<LiveControlDestination> { entry ->
        val destination = entry.toRoute<LiveControlDestination>()
        LiveControlRoute(
            matchId = destination.matchId,
            onBack = { navController.popBackStack() },
            onShare = { onShare("Falcons vs Titans · control en vivo en LeagueHub") },
            onFinished = { navController.navigate(PublishedResultDestination(it)) }
        )
    }

    composable<PublishedResultDestination> { entry ->
        val destination = entry.toRoute<PublishedResultDestination>()
        PublishedResultRoute(
            matchId = destination.matchId,
            onMatchCenter = { navController.navigate(MatchCenterDestination(it)) },
            onDashboard = {
                navController.navigate(OrganizerDashboardDestination) {
                    popUpTo<OrganizerDashboardDestination> { inclusive = true }
                }
            }
        )
    }
}
