package com.leaguehub.app.feature.matches.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.feature.matches.model.MatchUiModel

@Composable
fun CalendarRoute(onNavigate: (PlayerDestination) -> Unit, onMatchClick: (MatchUiModel) -> Unit, viewModel: CalendarViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalendarScreen(state.month, state.selectedDate, state.today, state.monthMatches, state.displayedMatches, state.onlyMyTeam,
        onMonthChange = { viewModel.onEvent(CalendarEvent.MonthChanged(it)) },
        onDateSelect = { viewModel.onEvent(CalendarEvent.DateSelected(it)) },
        onShowMonth = { viewModel.onEvent(CalendarEvent.ShowMonthClicked) },
        onFilterToggle = { viewModel.onEvent(CalendarEvent.MyTeamFilterToggled) },
        onMatchClick = onMatchClick, onNavigate = onNavigate)
}
