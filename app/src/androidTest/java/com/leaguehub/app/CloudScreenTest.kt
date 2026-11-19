package com.leaguehub.app
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.leaguehub.app.data.cloud.*
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.feature.local.cloud.*
import com.leaguehub.app.ui.theme.LeagueHubTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
class CloudScreenTest {
 @get:Rule val compose=createComposeRule()
 @Test fun conflictScreenKeepsTheChoiceExplicitAndRendersRecoveryActions() {
  val events=mutableListOf<CloudEvent>()
  val state=CloudState(ui=LocalUiState(name="Ana Torres",email="demo@leaguehub.app",loading=false),sync=CloudSyncStatus(configured=true,bound=true,phase="CONFLICT",message="Hay versiones distintas de una liga. Tus cambios no se han sobrescrito.",pending=1,issues=listOf(SyncIssue("league","Liga de demostración",true,"Otro teléfono cambió esta liga."))))
  compose.setContent { LeagueHubTheme { CloudScreen(state) { events+=it } } }
  compose.onNodeWithText("CUENTA CONECTADA").assertIsDisplayed()
  compose.onNodeWithTag("local-list").performScrollToNode(hasText("Usar versión de la nube"))
  val base=InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
  if(base!=null) { val dir=File(base,"firebase-screens").apply { mkdirs() };File(dir,"sincronizacion.png").outputStream().use { compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG,100,it) } }
  compose.onNodeWithText("Usar versión de la nube").performClick()
  assertTrue(events.contains(CloudEvent.AskResolve(Resolution("league",false))))
  assertFalse(events.contains(CloudEvent.ConfirmResolve))
 }
}
