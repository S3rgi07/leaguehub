package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.league.model.StandingUiModel

val fakeStandings = listOf(
    StandingUiModel(
        position = 1,
        team = wolves,
        played = 8,
        goalDifference = 12,
        points = 21
    ),
    StandingUiModel(
        position = 2,
        team = falcons,
        played = 8,
        goalDifference = 9,
        points = 19
    ),
    StandingUiModel(
        position = 3,
        team = titans,
        played = 8,
        goalDifference = 5,
        points = 16
    ),
    StandingUiModel(
        position = 4,
        team = atleticoNova,
        played = 8,
        goalDifference = 2,
        points = 14
    ),
    StandingUiModel(
        position = 5,
        team = campusUnited,
        played = 8,
        goalDifference = -1,
        points = 11
    )
)