package com.leaguehub.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.leaguehub.app.feature.player.PlayerApp
import com.leaguehub.app.ui.theme.LeagueHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeagueHubTheme {
                PlayerApp(
                    onOpenMap = { match, directions ->
                        val query = match.venueDetails?.mapQuery ?: match.venue
                        val uri = Uri.Builder().scheme("https").authority("www.google.com")
                            .appendPath("maps").appendPath(if (directions) "dir" else "search")
                            .appendQueryParameter("api", "1")
                            .appendQueryParameter(if (directions) "destination" else "query", query)
                            .build()
                        launchExternal(Intent(Intent.ACTION_VIEW, uri))
                    },
                    onShare = { text ->
                        launchExternal(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }, "Compartir con"))
                    }
                )
            }
        }
    }

    private fun launchExternal(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "No hay una aplicación disponible para esta acción.", Toast.LENGTH_LONG).show()
        }
    }
}
