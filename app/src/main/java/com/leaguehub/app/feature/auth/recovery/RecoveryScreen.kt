package com.leaguehub.app.feature.auth.recovery
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun RecoveryScreen(state:RecoveryState,onEvent:(RecoveryEvent)->Unit) {
 Column(Modifier.fillMaxSize().padding(WindowInsets.safeDrawing.asPaddingValues()).verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)) {
  Text(state.title,style=MaterialTheme.typography.headlineMedium);Text(state.description);Text(state.instructions)
  Button(onClick={onEvent(RecoveryEvent.BackClicked)}) { Text("Volver al acceso") }
 }
}
