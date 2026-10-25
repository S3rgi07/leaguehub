package com.leaguehub.app.feature.matches.calendar

import com.leaguehub.app.data.fake.fakeToday
import com.leaguehub.app.feature.matches.model.MatchUiModel
import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val month: YearMonth = YearMonth.of(2026, 9),
    val selectedDate: LocalDate? = null,
    val today: LocalDate = fakeToday,
    val monthMatches: List<MatchUiModel> = emptyList(),
    val displayedMatches: List<MatchUiModel> = emptyList(),
    val onlyMyTeam: Boolean = false
)
