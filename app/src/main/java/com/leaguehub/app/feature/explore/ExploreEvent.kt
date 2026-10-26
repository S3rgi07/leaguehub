package com.leaguehub.app.feature.explore

sealed interface ExploreEvent {
    data class SearchChanged(val query: String) : ExploreEvent
    data class CategoryChanged(val category: ExploreCategory) : ExploreEvent
}
