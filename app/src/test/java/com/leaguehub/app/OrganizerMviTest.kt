package com.leaguehub.app

import com.leaguehub.app.feature.matches.matchcenter.MatchCenterEvent
import com.leaguehub.app.feature.matches.matchcenter.MatchCenterState
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab
import com.leaguehub.app.feature.organizer.MatchWorkflowRepository
import com.leaguehub.app.feature.organizer.dashboard.OrganizerDashboardEvent
import com.leaguehub.app.feature.organizer.dashboard.OrganizerDashboardViewModel
import com.leaguehub.app.feature.organizer.live.LiveControlEvent
import com.leaguehub.app.feature.organizer.live.LiveControlViewModel
import com.leaguehub.app.feature.organizer.matchmanagement.MatchManagementEvent
import com.leaguehub.app.feature.organizer.matchmanagement.MatchManagementViewModel
import com.leaguehub.app.feature.organizer.scan.ScanReportEvent
import com.leaguehub.app.feature.organizer.scan.ScanReportViewModel
import com.leaguehub.app.feature.organizer.validation.events.EventValidationEvent
import com.leaguehub.app.feature.organizer.validation.events.EventValidationViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrganizerMviTest {
    @Test
    fun dashboardSelectionIsHandledByState() {
        val viewModel = OrganizerDashboardViewModel()

        viewModel.onEvent(OrganizerDashboardEvent.ReportSelected("wolves_condores"))

        assertEquals("wolves_condores", viewModel.state.value.selectedReportId)
    }

    @Test
    fun scanUpdatesTheSharedScoreAndCompletesProcessing() {
        val repository = MatchWorkflowRepository()
        val viewModel = ScanReportViewModel(repository)

        viewModel.onEvent(ScanReportEvent.HomeScoreChanged(3))
        viewModel.onEvent(ScanReportEvent.ProcessClicked)

        assertEquals(3, repository.state.value.homeScore)
        assertTrue(viewModel.state.value.isProcessed)
    }

    @Test
    fun selectingIncompleteRosterMakesItReadyForLiveControl() {
        val viewModel = MatchManagementViewModel(MatchWorkflowRepository())
        val incompleteTeam = viewModel.state.value.data.rosters.first { it.missing > 0 }

        viewModel.onEvent(MatchManagementEvent.RosterSelected(incompleteTeam.team.id))
        viewModel.onEvent(MatchManagementEvent.StartLiveClicked)

        assertTrue(viewModel.state.value.data.rosters.none { it.missing > 0 })
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test
    fun liveGoalUpdatesScoreAndTimeline() {
        val repository = MatchWorkflowRepository()
        val viewModel = LiveControlViewModel(repository)
        val originalEvents = viewModel.state.value.recentEvents.size

        viewModel.onEvent(LiveControlEvent.GoalClicked)

        assertEquals(3, viewModel.state.value.homeScore)
        assertEquals(originalEvents + 1, viewModel.state.value.recentEvents.size)
    }

    @Test
    fun publishingValidationUpdatesSharedWorkflow() {
        val repository = MatchWorkflowRepository()
        val viewModel = EventValidationViewModel(repository)

        viewModel.onEvent(EventValidationEvent.PublishClicked)

        assertTrue(viewModel.state.value.isPublished)
        assertTrue(repository.state.value.isPublished)
    }

    @Test
    fun matchCenterStateSupportsAllFourTabs() {
        val state = MatchCenterState()
        val tabs = MatchCenterTab.entries

        assertEquals(4, tabs.size)
        assertEquals(MatchCenterTab.SUMMARY, state.selectedTab)
        assertTrue(MatchCenterEvent.TabSelected(MatchCenterTab.GALLERY).tab == MatchCenterTab.GALLERY)
    }
}
