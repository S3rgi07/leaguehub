package com.leaguehub.app.feature.matches.calendar

import java.time.LocalDate
import java.time.YearMonth

sealed interface CalendarEvent {
    data class MonthChanged(val month: YearMonth) : CalendarEvent
    data class DateSelected(val date: LocalDate) : CalendarEvent
    data object ShowMonthClicked : CalendarEvent
    data object MyTeamFilterToggled : CalendarEvent
}
