package com.leaguehub.app.feature.matches.model

/** Display information only; the route preview is illustrative, not live navigation. */
data class VenueUiModel(
    val address: String,
    val accessTime: String,
    val travelTime: String,
    val distance: String,
    val arrivalAdvice: String,
    val mapQuery: String
)
