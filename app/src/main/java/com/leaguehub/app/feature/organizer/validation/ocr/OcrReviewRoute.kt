package com.leaguehub.app.feature.organizer.validation.ocr

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
fun OcrReviewRoute(
    matchId: String,
    onBack: () -> Unit,
    onValidated: (String) -> Unit
) {
    val application = LocalContext.current.applicationContext as LeagueHubApplication
    val viewModel: OcrReviewViewModel = viewModel(
        key = "ocr-$matchId",
        factory = viewModelFactory {
            initializer { OcrReviewViewModel(application.matchWorkflowRepository) }
        }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isValidated) {
        if (state.isValidated) onValidated(matchId)
    }
    OcrReviewScreen(
        data = state.data,
        onBackClick = onBack,
        onEditRefereeClick = { viewModel.onEvent(OcrReviewEvent.RefereeEditClicked) },
        onEditScoreClick = { viewModel.onEvent(OcrReviewEvent.ScoreEditClicked) },
        onValidateClick = { viewModel.onEvent(OcrReviewEvent.ValidateClicked) }
    )
}
