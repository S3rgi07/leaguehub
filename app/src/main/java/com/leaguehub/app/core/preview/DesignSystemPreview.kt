package com.leaguehub.app.core.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.core.components.LeagueHubCard
import com.leaguehub.app.core.components.LeagueHubTextField
import com.leaguehub.app.ui.theme.LeagueHubTheme
import androidx.compose.ui.graphics.Color
import com.leaguehub.app.core.components.MatchScoreboard
import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.team.model.TeamUiModel
import com.leaguehub.app.data.fake.falcons
import com.leaguehub.app.data.fake.titans

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun DesignSystemPreview() {
    LeagueHubTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "LeagueHub",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "La cancha, siempre conectada.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            LeagueHubButton(
                text = "Continuar",
                onClick = {}
            )

            LeagueHubTextField(
                value = "sofia@leaguehub.app",
                onValueChange = {},
                label = "Correo electrónico"
            )

            LeagueHubCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Próximo partido",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Falcons vs Titans",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
            MatchScoreboard(
                homeTeam = falcons,
                awayTeam = titans,
                homeScore = 2,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = "75'"
            )
        }
    }
}