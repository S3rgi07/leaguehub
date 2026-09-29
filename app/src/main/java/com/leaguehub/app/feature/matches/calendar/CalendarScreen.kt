package com.leaguehub.app.feature.matches.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.*
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.ui.theme.LeagueHubTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Includes adjacent-month days to keep a complete Monday-first grid. */
fun calendarDays(month: YearMonth): List<LocalDate> {
    val first = month.atDay(1)
    val offset = first.dayOfWeek.value - 1
    val count = ((offset + month.lengthOfMonth() + 6) / 7) * 7
    return List(count) { first.minusDays(offset.toLong()).plusDays(it.toLong()) }
}
fun matchesInMonth(matches: List<MatchUiModel>, month: YearMonth): List<MatchUiModel> =
    matches.filter { it.localDate?.let(YearMonth::from) == month }

@Composable
fun CalendarScreen(
    month: YearMonth,
    selectedDate: LocalDate?,
    today: LocalDate,
    monthMatches: List<MatchUiModel>,
    displayedMatches: List<MatchUiModel>,
    onlyMyTeam: Boolean,
    onMonthChange: (YearMonth) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onShowMonth: () -> Unit,
    onFilterToggle: () -> Unit,
    onMatchClick: (MatchUiModel) -> Unit,
    onNavigate: (PlayerDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = Locale.forLanguageTag("es-GT")
    PlayerScaffold(PlayerDestination.CALENDAR, onNavigate, modifier) {
        item { PlayerTopBar("Calendario", if (onlyMyTeam) "Tus partidos" else "Tus partidos y la liga") {
            LeagueIconButton(R.drawable.lh_filter, if (onlyMyTeam) "Mostrar toda la liga" else "Mostrar solo mi equipo", onFilterToggle)
        } }
        item {
            LeagueHubCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    LeagueIconButton(R.drawable.lh_back, "Mes anterior", { onMonthChange(month.minusMonths(1)) })
                    Text(month.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale)).replaceFirstChar { it.titlecase(locale) },
                        Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    LeagueIconButton(R.drawable.lh_chevron, "Mes siguiente", { onMonthChange(month.plusMonths(1)) })
                }
                Row(Modifier.fillMaxWidth()) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                        Box(Modifier.weight(1f).height(24.dp), contentAlignment = Alignment.Center) {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                calendarDays(month).chunked(7).forEach { week ->
                    Row(Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            val selected = day == selectedDate
                            val hasMatch = monthMatches.any { it.localDate == day }
                            Column(
                                Modifier.weight(1f).height(40.dp)
                                    .selectable(selected, onClick = { onDateSelect(day) }, role = Role.Button)
                                    .semantics { contentDescription = day.format(DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", locale)) + if (hasMatch) ", hay partido" else "" },
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
                            ) {
                                Box(Modifier.size(30.dp).background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(day.dayOfMonth.toString(), style = MaterialTheme.typography.labelMedium,
                                        color = when {
                                            selected -> MaterialTheme.colorScheme.onPrimary
                                            YearMonth.from(day) != month -> MaterialTheme.colorScheme.onSurfaceVariant
                                            else -> MaterialTheme.colorScheme.onSurface
                                        })
                                }
                                Box(Modifier.size(4.dp).background(if (hasMatch && !selected) MaterialTheme.colorScheme.secondary else Color.Transparent, CircleShape))
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    LeagueBadge("${monthMatches.size} PARTIDOS", color = MaterialTheme.colorScheme.secondary, icon = R.drawable.lh_calendar)
                    MutedText("Hoy · ${today.format(DateTimeFormatter.ofPattern("d MMM", locale))}")
                }
            }
        }
        item { SectionHeading(if (selectedDate == null) "Partidos del mes" else "Partidos · ${selectedDate.dayOfMonth}", "Ver mes", onShowMonth) }
        if (displayedMatches.isEmpty()) item { EmptyContent("No hay partidos para esta selección.") }
        items(displayedMatches, key = { it.id }) { match -> MatchListCard(match, { onMatchClick(match) }) }
    }
}

@Preview(name = "P08 · Calendario", widthDp = 390, heightDp = 844)
@Composable
private fun CalendarPreview() {
    LeagueHubTheme { CalendarScreen(YearMonth.of(2026, 9), null, fakeToday, fakePlayerMatches, fakePlayerMatches, false, {}, {}, {}, {}, {}, {}) }
}

@Preview(name = "P08 · Mes vacío", widthDp = 390, heightDp = 844)
@Composable
private fun EmptyCalendarPreview() {
    LeagueHubTheme { CalendarScreen(YearMonth.of(2026, 10), null, fakeToday, emptyList(), emptyList(), false, {}, {}, {}, {}, {}, {}) }
}
