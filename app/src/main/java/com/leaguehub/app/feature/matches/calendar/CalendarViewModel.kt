package com.leaguehub.app.feature.matches.calendar

import androidx.lifecycle.ViewModel
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    private val allMatches = (fakePlayerMatches + fakeRecentResults).distinctBy { it.id }
    private val _state = MutableStateFlow(buildState(YearMonth.of(2026, 9), null, false))
    val state: StateFlow<CalendarState> = _state.asStateFlow()

    fun onEvent(event: CalendarEvent) {
        val current = _state.value
        _state.value = when (event) {
            is CalendarEvent.MonthChanged -> buildState(event.month, null, current.onlyMyTeam)
            is CalendarEvent.DateSelected -> buildState(YearMonth.from(event.date), event.date, current.onlyMyTeam)
            CalendarEvent.ShowMonthClicked -> buildState(current.month, null, current.onlyMyTeam)
            CalendarEvent.MyTeamFilterToggled -> buildState(current.month, current.selectedDate, !current.onlyMyTeam)
        }
    }

    private fun buildState(month: YearMonth, selected: LocalDate?, onlyMyTeam: Boolean): CalendarState {
        val monthMatches = matchesInMonth(allMatches, month).filter {
            !onlyMyTeam || it.homeTeam.id == sofia.teamId || it.awayTeam.id == sofia.teamId
        }
        val displayed = monthMatches
            .filter { selected == null || it.localDate == selected }
            .sortedWith(compareBy<MatchUiModel> { it.localDate?.isBefore(fakeToday) == true }.thenBy { it.localDate })
        return CalendarState(month, selected, fakeToday, monthMatches, displayed, onlyMyTeam)
    }
}
