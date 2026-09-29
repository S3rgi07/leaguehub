package com.leaguehub.app.data.fake

import com.leaguehub.app.feature.home.model.MatchPredictionUiModel
import com.leaguehub.app.feature.league.model.LeagueNewsUiModel
import java.time.LocalDate

val fakeToday: LocalDate = LocalDate.of(2026, 9, 9)
val fakePrediction = MatchPredictionUiModel(
    "Duelo de alta intensidad",
    "Falcons llega con 68% de probabilidad de sumar puntos. La clave: recuperar en campo rival durante los primeros 20 minutos.",
    68
)
val fakeLeagueNews = listOf(
    LeagueNewsUiModel(
        "matchday_8", "La jornada 8 define los puestos de clasificación", "Hace 3 h · 4 min lectura",
        "La Liga Universitaria llega a la jornada 8 con Wolves al frente y Falcons en el segundo puesto. " +
            "El encuentro entre Falcons y Titans será clave en la lucha por la clasificación. " +
            "Consulta la tabla y el calendario para seguir los próximos encuentros."
    )
)
