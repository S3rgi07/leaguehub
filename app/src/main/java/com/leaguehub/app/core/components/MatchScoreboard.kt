package com.leaguehub.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.team.model.TeamUiModel
import androidx.compose.foundation.layout.padding

@Composable
fun MatchScoreboard(
    homeTeam: TeamUiModel,
    awayTeam: TeamUiModel,
    homeScore: Int?,
    awayScore: Int?,
    status: MatchStatus,
    modifier: Modifier = Modifier,
    minute: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MatchStatusLabel(
            status = status
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TeamInfo(
                team = homeTeam
            )

            ScoreInfo(
                homeScore = homeScore,
                awayScore = awayScore,
                status = status,
                minute = minute
            )

            TeamInfo(
                team = awayTeam
            )
        }
    }
}

@Composable
private fun TeamInfo(
    team: TeamUiModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = team.color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.abbreviation,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = team.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ScoreInfo(
    homeScore: Int?,
    awayScore: Int?,
    status: MatchStatus,
    minute: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val scoreText = when (status) {

            MatchStatus.SCHEDULED -> "VS"

            MatchStatus.LIVE,
            MatchStatus.FINISHED -> {
                "${homeScore ?: 0} - ${awayScore ?: 0}"
            }
        }

        Text(
            text = scoreText,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (status == MatchStatus.LIVE && minute != null) {

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "MIN $minute",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MatchStatusLabel(
    status: MatchStatus
) {
    val text = when (status) {
        MatchStatus.SCHEDULED -> "PROGRAMADO"
        MatchStatus.LIVE -> "EN VIVO"
        MatchStatus.FINISHED -> "FINAL"
    }

    val color = when (status) {
        MatchStatus.SCHEDULED -> MaterialTheme.colorScheme.secondary
        MatchStatus.LIVE -> MaterialTheme.colorScheme.primary
        MatchStatus.FINISHED -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}