package com.leaguehub.app.feature.onboarding.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leaguehub.app.R
import com.leaguehub.app.core.components.LeagueHubButton
import com.leaguehub.app.feature.onboarding.welcome.components.OnboardingFeatureItem
import com.leaguehub.app.feature.onboarding.welcome.components.OnboardingHero
import com.leaguehub.app.ui.theme.LeagueHubTheme

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 24.dp,
                vertical = 24.dp
            )
    ) {

        OnboardingHero()

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "La cancha,",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "siempre conectada",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OnboardingFeatureItem(
                iconRes = R.drawable.ic_live_results,
                title = "Resultados en vivo",
                description = "Minuto a minuto sin perderte nada"
            )

            OnboardingFeatureItem(
                iconRes = R.drawable.ic_statistics,
                title = "Estadísticas reales",
                description = "Rendimiento, tabla y goleadores"
            )

            OnboardingFeatureItem(
                iconRes = R.drawable.ic_scan,
                title = "Organiza en minutos",
                description = "Digitaliza actas con asistencia IA"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PageIndicator(
            selectedPage = 0,
            pageCount = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        LeagueHubButton(
            text = "Comenzar",
            onClick = onStartClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ya tengo una cuenta",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PageIndicator(
    selectedPage: Int,
    pageCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pageCount) { index ->
            Surface(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(8.dp),
                shape = CircleShape,
                color = if (index == selectedPage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ) {}
        }
    }
}

@Preview(
    name = "Onboarding",
    showBackground = true
)
@Composable
private fun OnboardingScreenPreview() {
    LeagueHubTheme {
        OnboardingScreen()
    }
}