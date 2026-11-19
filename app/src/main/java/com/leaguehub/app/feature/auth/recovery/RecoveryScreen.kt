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
  if(state.enabled) {
   OutlinedTextField(state.email,{onEvent(RecoveryEvent.EmailChanged(it))},label={Text("Correo electrónico")},singleLine=true,enabled=!state.loading,modifier=Modifier.fillMaxWidth())
   Button(onClick={onEvent(RecoveryEvent.SendClicked)},enabled=!state.loading) { Text(if(state.loading) "Solicitando enlace…" else "Enviar enlace de recuperación") }
  } else Text("Firebase no está configurado en esta compilación. Puedes cambiar tu contraseña desde una sesión local abierta usando la contraseña actual.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  state.message?.let { Text(it,color=MaterialTheme.colorScheme.primary) };state.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
  TextButton(onClick={onEvent(RecoveryEvent.BackClicked)}) { Text("Volver al acceso") }
 }
}
