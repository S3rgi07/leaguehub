package com.leaguehub.app.feature.explore

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel : ViewModel() {
    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    fun onEvent(event: ExploreEvent) {
        val current = _state.value
        _state.value = when (event) {
            is ExploreEvent.SearchChanged -> filtered(event.query, current.selectedCategory)
            is ExploreEvent.CategoryChanged -> filtered(current.searchQuery, event.category)
        }
    }

    private fun filtered(query: String, category: ExploreCategory): ExploreState {
        fun List<ExploreItemUiModel>.matching(expected: ExploreCategory) = filter { item ->
            (category == ExploreCategory.ALL || category == expected) &&
                (query.isBlank() || item.name.contains(query.trim(), ignoreCase = true) ||
                    item.description.contains(query.trim(), ignoreCase = true))
        }
        return ExploreState(
            searchQuery = query,
            selectedCategory = category,
            featuredLeagues = defaultExploreLeagues.matching(ExploreCategory.LEAGUES),
            popularTeams = defaultExploreTeams.matching(ExploreCategory.TEAMS),
            suggestedPlayers = defaultExplorePlayers.matching(ExploreCategory.PLAYERS)
        )
    }
}
