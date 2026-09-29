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
    ),
    StandingUiModel(6, deportivoSur, 8, -4, 8),
    StandingUiModel(7, racing12, 8, -11, 4),
    StandingUiModel(8, leones, 8, -13, 3)
)

// Separate demo splits. General values above stay shared with the other features.
val fakeHomeStandings = fakeStandings.map { it.copy(played = 4, points = (it.points + 1) / 2, goalDifference = it.goalDifference / 2) }
val fakeAwayStandings = fakeStandings.mapIndexed { index, row ->
    row.copy(played = 4, points = row.points - fakeHomeStandings[index].points,
        goalDifference = row.goalDifference - fakeHomeStandings[index].goalDifference)
}
