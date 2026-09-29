package com.leaguehub.app

import com.leaguehub.app.data.fake.fakePlayerMatches
import com.leaguehub.app.feature.matches.calendar.calendarDays
import com.leaguehub.app.feature.matches.calendar.matchesInMonth
import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class PlayerCalendarTest {
    @Test fun september2026AlignsMondayAndIncludesEveryDayOnce() {
        val days = calendarDays(YearMonth.of(2026, 9))
        assertEquals(LocalDate.of(2026, 8, 31), days.first())
        assertEquals(DayOfWeek.MONDAY, days.first().dayOfWeek)
        assertEquals(DayOfWeek.SUNDAY, days.last().dayOfWeek)
        assertEquals(35, days.size)
        assertEquals(30, days.count { it.monthValue == 9 })
        assertEquals(days.size, days.distinct().size)
    }

    @Test fun leapFebruaryContainsLeapDay() {
        assertTrue(calendarDays(YearMonth.of(2028, 2)).contains(LocalDate.of(2028, 2, 29)))
    }

    @Test fun sixWeekMonthDoesNotDropTheLastWeekend() {
        val days = calendarDays(YearMonth.of(2026, 3))
        assertEquals(42, days.size)
        assertTrue(days.contains(LocalDate.of(2026, 3, 31)))
    }

    @Test fun matchesAreFilteredByYearAndMonthNotDisplayLabel() {
        assertEquals(4, matchesInMonth(fakePlayerMatches, YearMonth.of(2026, 9)).size)
        assertTrue(matchesInMonth(fakePlayerMatches, YearMonth.of(2027, 9)).isEmpty())
        assertTrue(matchesInMonth(fakePlayerMatches, YearMonth.of(2026, 10)).isEmpty())
    }
}
