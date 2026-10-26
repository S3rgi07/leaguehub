package com.leaguehub.app

import com.leaguehub.app.data.fake.fakeAwayStandings
import com.leaguehub.app.data.fake.fakeHomeStandings
import com.leaguehub.app.data.repository.FollowedTeamsRepository
import com.leaguehub.app.feature.explore.ExploreCategory
import com.leaguehub.app.feature.explore.ExploreEvent
import com.leaguehub.app.feature.explore.ExploreViewModel
import com.leaguehub.app.feature.league.standings.StandingsEvent
import com.leaguehub.app.feature.league.standings.StandingsScope
import com.leaguehub.app.feature.league.standings.StandingsViewModel
import com.leaguehub.app.feature.matches.calendar.CalendarEvent
import com.leaguehub.app.feature.matches.calendar.CalendarViewModel
import com.leaguehub.app.feature.notifications.NotificationCategory
import com.leaguehub.app.feature.notifications.NotificationsEvent
import com.leaguehub.app.feature.notifications.NotificationsViewModel
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerMviTest {
    @Test
    fun standingsEventsReplaceScopeAndRows() {
        val viewModel = StandingsViewModel()

        viewModel.onEvent(StandingsEvent.ScopeChanged(StandingsScope.HOME))
        assertEquals(StandingsScope.HOME, viewModel.state.value.scope)
        assertEquals(fakeHomeStandings, viewModel.state.value.standings)

        viewModel.onEvent(StandingsEvent.ScopeChanged(StandingsScope.AWAY))
        assertEquals(StandingsScope.AWAY, viewModel.state.value.scope)
        assertEquals(fakeAwayStandings, viewModel.state.value.standings)
    }

    @Test
    fun calendarEventsPreserveFiltersAndSelection() {
        val viewModel = CalendarViewModel()
        val selectedDate = LocalDate.of(2026, 9, 20)

        viewModel.onEvent(CalendarEvent.MyTeamFilterToggled)
        viewModel.onEvent(CalendarEvent.DateSelected(selectedDate))

        assertTrue(viewModel.state.value.onlyMyTeam)
        assertEquals(selectedDate, viewModel.state.value.selectedDate)
        assertTrue(viewModel.state.value.displayedMatches.all { it.localDate == selectedDate })

        viewModel.onEvent(CalendarEvent.MonthChanged(YearMonth.of(2026, 10)))
        assertTrue(viewModel.state.value.onlyMyTeam)
        assertTrue(viewModel.state.value.displayedMatches.isEmpty())
    }

    @Test
    fun notificationsCanBeFilteredAndMarkedRead() {
        val viewModel = NotificationsViewModel()
        assertEquals(4, viewModel.state.value.unreadCount)

        viewModel.onEvent(NotificationsEvent.CategoryChanged(NotificationCategory.MATCHES))
        assertEquals(NotificationCategory.MATCHES, viewModel.state.value.selectedCategory)
        assertTrue(viewModel.state.value.visibleTodayNotifications.all {
            it.kind.name in setOf("GOAL", "REMINDER", "PREDICTION")
        })

        viewModel.onEvent(NotificationsEvent.NotificationClicked("1"))
        assertEquals(3, viewModel.state.value.unreadCount)

        viewModel.onEvent(NotificationsEvent.MarkAllReadClicked)
        assertEquals(0, viewModel.state.value.unreadCount)
    }

    @Test
    fun exploreSearchAndCategoryProduceOnlyMatchingItems() {
        val viewModel = ExploreViewModel()

        viewModel.onEvent(ExploreEvent.CategoryChanged(ExploreCategory.TEAMS))
        assertTrue(viewModel.state.value.featuredLeagues.isEmpty())
        assertTrue(viewModel.state.value.suggestedPlayers.isEmpty())
        assertTrue(viewModel.state.value.popularTeams.isNotEmpty())

        viewModel.onEvent(ExploreEvent.SearchChanged("Titans"))
        assertEquals(listOf("titans"), viewModel.state.value.popularTeams.map { it.id })
    }

    @Test
    fun followedTeamsRepositoryIsSharedSourceOfTruth() {
        val repository = FollowedTeamsRepository(emptySet())
        assertFalse(repository.isFollowing("falcons"))

        repository.toggle("falcons")
        assertTrue(repository.isFollowing("falcons"))
        assertEquals(setOf("falcons"), repository.followedTeamIds.value)

        repository.toggle("falcons")
        assertFalse(repository.isFollowing("falcons"))
    }
}
