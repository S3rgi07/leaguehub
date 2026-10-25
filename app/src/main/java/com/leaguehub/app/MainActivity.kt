package com.leaguehub.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.navigation.LeagueHubNavHost
import com.leaguehub.app.ui.theme.LeagueHubTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LeagueHubTheme {
                LeagueHubNavHost(
                    onOpenMap = { match, directions ->
                        openMatchMap(match, directions)
                    },
                    onShare = { text ->
                        shareText(text)
                    }
                )
            }
        }
    }

    private fun openMatchMap(
        match: MatchUiModel,
        directions: Boolean
    ) {
        val query = match.venueDetails?.mapQuery ?: match.venue

        val uri = Uri.Builder()
            .scheme("https")
            .authority("www.google.com")
            .appendPath("maps")
            .appendPath(if (directions) "dir" else "search")
            .appendQueryParameter("api", "1")
            .appendQueryParameter(
                if (directions) "destination" else "query",
                query
            )
            .build()

        launchExternal(
            Intent(Intent.ACTION_VIEW, uri)
        )
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        launchExternal(
            Intent.createChooser(intent, "Compartir con")
        )
    }

    private fun launchExternal(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "No hay una aplicación disponible para esta acción.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
