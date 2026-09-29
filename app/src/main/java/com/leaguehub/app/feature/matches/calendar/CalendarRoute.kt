package com.leaguehub.app.feature.matches.calendar

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.leaguehub.app.core.components.PlayerDestination
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarRoute(onNavigate: (PlayerDestination) -> Unit, onMatchClick: (MatchUiModel) -> Unit) {
    var monthValue by rememberSaveable { mutableStateOf("2026-09") }
    var selectedValue by rememberSaveable { mutableStateOf<String?>(null) }
    var onlyMyTeam by rememberSaveable { mutableStateOf(false) }
    val month = YearMonth.parse(monthValue)
    val selected = selectedValue?.let(LocalDate::parse)
    val allMatches = (fakePlayerMatches + fakeRecentResults).distinctBy { it.id }
    val monthMatches = matchesInMonth(allMatches, month).filter { !onlyMyTeam || it.homeTeam.id == sofia.teamId || it.awayTeam.id == sofia.teamId }
    val displayed = monthMatches.filter { selected == null || it.localDate == selected }
        .sortedWith(compareBy<MatchUiModel> { it.localDate?.isBefore(fakeToday) == true }.thenBy { it.localDate })
    CalendarScreen(month, selected, fakeToday, monthMatches, displayed, onlyMyTeam,
        onMonthChange = { monthValue = it.toString(); selectedValue = null },
        onDateSelect = { monthValue = YearMonth.from(it).toString(); selectedValue = it.toString() },
        onShowMonth = { selectedValue = null },
        onFilterToggle = { onlyMyTeam = !onlyMyTeam },
        onMatchClick = onMatchClick, onNavigate = onNavigate)
}
