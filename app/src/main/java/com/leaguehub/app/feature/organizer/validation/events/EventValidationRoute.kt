package com.leaguehub.app.feature.organizer.validation.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leaguehub.app.LeagueHubApplication

@Composable
fun EventValidationRoute(
    matchId: String,
    onBack: () -> Unit,
    onCorrectData: () -> Unit,
    onPublished: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: EventValidationViewModel = viewModel(
        key = "validation-$matchId",
        factory = viewModelFactory {
            initializer { EventValidationViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isPublished) {
        if (state.isPublished) onPublished(matchId)
    }
    EventValidationScreen(
        events = state.events,
        homeScore = state.homeScore,
        awayScore = state.awayScore,
        lineupCount = state.lineupCount,
        onBackClick = onBack,
        onEventEditClick = { viewModel.onEvent(EventValidationEvent.EventEditClicked(it)) },
        onAddEventClick = { viewModel.onEvent(EventValidationEvent.AddEventClicked) },
        onCorrectDataClick = {
            viewModel.onEvent(EventValidationEvent.CorrectDataClicked)
            onCorrectData()
        },
        onPublishClick = { viewModel.onEvent(EventValidationEvent.PublishClicked) }
    )
}
