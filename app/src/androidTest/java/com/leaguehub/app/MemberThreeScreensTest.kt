package com.leaguehub.app

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.matchcenter.MatchCenterScreen
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab
import com.leaguehub.app.feature.organizer.dashboard.OrganizerDashboardScreen
import com.leaguehub.app.feature.organizer.matchmanagement.MatchManagementScreen
import com.leaguehub.app.feature.organizer.model.ScanStatus
import com.leaguehub.app.feature.organizer.scan.ScanReportScreen
import com.leaguehub.app.ui.theme.LeagueHubTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Checks the assigned views on a real Compose layout and saves images for visual review. */
class MemberThreeScreensTest {
    @get:Rule val compose = createComposeRule()

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { LeagueHubTheme(content) }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.filesDir, "member-three-previews")
        directory.mkdirs()
        File(directory, "$name.png").outputStream().use { stream ->
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }

    private fun match(tab: MatchCenterTab) {
        show { MatchCenterScreen(falconsVsTitans, universityLeague.name, tab, fakeMatchEvents,
            fakeLineups, falcons.id, fakeMatchStatistics, fakeMomentum) }
    }

    @Test fun summaryShowsTimelineAndFollowAction() {
        match(MatchCenterTab.SUMMARY)
        compose.onNodeWithText("Cronología").assertIsDisplayed()
        capture("P02")
        compose.onNodeWithText("Seguir relato en vivo").performScrollTo().assertIsDisplayed()
    }

    @Test fun lineupShowsFormationAndSubstitutes() {
        match(MatchCenterTab.LINEUP)
        compose.onNodeWithText("4–3–3").assertIsDisplayed()
        capture("P03")
        compose.onNodeWithText("Suplentes").performScrollTo().assertIsDisplayed()
    }

    @Test fun statisticsShowComparisonAndMomentum() {
        match(MatchCenterTab.STATS)
        compose.onNodeWithText("Posesión").assertIsDisplayed()
        capture("P04")
        compose.onNodeWithText("MOMENTUM").performScrollTo().assertIsDisplayed()
    }

    @Test fun dashboardShowsPendingReportsAndScanAction() {
        show { OrganizerDashboardScreen(universityLeague.name, falconsVsTitans, fakeMatchdaySummary, fakePendingReports) }
        compose.onNodeWithText("Panel de liga").assertIsDisplayed()
        capture("O01")
        compose.onNodeWithText("Escanear nueva acta").performScrollTo().assertIsDisplayed()
    }

    @Test fun managementShowsIncompleteRosterWarning() {
        show { MatchManagementScreen(fakeMatchManagement) }
        capture("O02")
        compose.onNodeWithText("Completa la alineación de Titans antes del inicio.").performScrollTo().assertIsDisplayed()
    }

    @Test fun detectedReportCanBeProcessed() {
        var processed = false
        show { ScanReportScreen(falconsVsTitans, ScanStatus.DETECTED, 2, 1, onProcess = { processed = true }) }
        compose.onNodeWithText("DOCUMENTO DETECTADO").assertIsDisplayed()
        capture("O03")
        compose.onNodeWithText("Procesar acta").performScrollTo().assertIsEnabled().performClick()
        assertEquals(true, processed)
    }

    @Test fun searchingReportCannotBeProcessed() {
        show { ScanReportScreen(falconsVsTitans, ScanStatus.SEARCHING, 0, 0) }
        compose.onNodeWithText("Procesar acta").performScrollTo().assertIsNotEnabled()
        capture("O03-searching")
    }

    @Test fun unreadableReportOffersRetry() {
        var retried = false
        show { ScanReportScreen(falconsVsTitans, ScanStatus.ERROR, 2, 1, onRetry = { retried = true }) }
        compose.onNodeWithText("Volver a capturar").performScrollTo().performClick()
        assertEquals(true, retried)
        capture("O03-error")
    }
}
