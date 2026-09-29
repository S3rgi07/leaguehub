package com.leaguehub.app

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.leaguehub.app.feature.player.PlayerApp
import com.leaguehub.app.ui.theme.LeagueHubTheme
import org.junit.Rule
import org.junit.Test
import java.io.File

class PlayerFlowTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun start() {
        compose.setContent { LeagueHubTheme { PlayerApp({ _, _ -> }, {}) } }
    }

    private fun screenshot(name: String) {
        compose.waitForIdle()
        val folder = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")?.let { File(it, "player-screens") }
            ?: File(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null), "player-screens")
        folder.mkdirs()
        File(folder, "$name.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test fun allSevenScreensCanBeReached() {
        start()
        compose.onNodeWithText("Hola, Sofía").assertIsDisplayed()
        screenshot("P01-inicio")
        compose.onNodeWithText("Ver mapa de la cancha").performScrollTo().performClick()
        compose.onNodeWithText("Detalle del partido").assertIsDisplayed()
        screenshot("P09-partido")
        compose.onNodeWithText("Tabla", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Tabla de posiciones").assertIsDisplayed()
        screenshot("P06-tabla")
        compose.onNodeWithText("Falcons", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Plantilla").performScrollTo().assertIsDisplayed()
        // Return to the header before capturing the initial view.
        compose.onNodeWithText("Falcons", useUnmergedTree = true).performScrollTo()
        screenshot("P11-equipo")
        compose.onNodeWithText("Sofía Méndez", useUnmergedTree = true).performScrollTo().performClick()
        compose.onNodeWithText("Goles", useUnmergedTree = true).assertIsDisplayed()
        screenshot("P07-perfil")
        compose.onAllNodesWithText("Partidos", useUnmergedTree = true).onLast().performClick()
        compose.onNodeWithText("Calendario").assertIsDisplayed()
        screenshot("P08-calendario")
        compose.onNodeWithText("Inicio", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Jornada 8 · Liga Universitaria GT", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Equipos destacados").assertIsDisplayed()
        screenshot("P10-liga")
    }

    @Test fun filtersAndEmptyCalendarWork() {
        start()
        compose.onNodeWithText("Tabla", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Local", useUnmergedTree = true).performClick()
        compose.onNodeWithText("Local").assertIsSelected()
        compose.onAllNodesWithText("Partidos", useUnmergedTree = true).onLast().performClick()
        compose.onNodeWithContentDescription("Mes siguiente").performClick()
        compose.onNodeWithText("Octubre 2026").assertIsDisplayed()
        compose.onNodeWithText("No hay partidos para esta selección.").performScrollTo().assertIsDisplayed()
    }
}
