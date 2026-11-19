package com.leaguehub.app.feature.local.settings
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
@Composable fun SettingsScreen(state:SettingsState,onEvent:(SettingsEvent)->Unit) {
 LocalFrame(state.ui,LocalSection.PROFILE,{onEvent(SettingsEvent.Common(it))},showLeague=false,showBack=true,title="Configuración") {
  item { HeroCard { Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
   PlayerAvatar(state.ui.name,size=60.dp);Column { Text(state.ui.name,style=MaterialTheme.typography.titleLarge);MutedText(state.ui.email);LeagueBadge(state.ui.roleLabel) }
  } } }
  item { SectionHeading("Cuenta y preferencias") }
  item { LeagueHubCard(Modifier.fillMaxWidth()) {
   SettingsRow("Editar mi nombre",R.drawable.lh_user) { onEvent(SettingsEvent.EditName) }
   SettingsRow("Cambiar contraseña",R.drawable.lh_shield) { onEvent(SettingsEvent.Password) }
   SettingsRow("Sincronización Firebase",R.drawable.lh_radio) { onEvent(SettingsEvent.Common(LocalEvent.OpenPage(LocalPage.CLOUD))) }
   SettingsRow("Notificaciones",R.drawable.lh_bell) { onEvent(SettingsEvent.Common(LocalEvent.OpenPage(LocalPage.ALERTS))) }
   SettingsRow("Apariencia: ${state.ui.appearance}",R.drawable.lh_spark) { onEvent(SettingsEvent.Common(LocalEvent.ChangeAppearance)) }
   SettingsRow("Privacidad y datos",R.drawable.lh_info) { onEvent(SettingsEvent.Privacy(true)) }
  } }
  item { SectionHeading("Modo y liga") }
  item { LeagueHubCard(Modifier.fillMaxWidth()) {
   SettingsRow(if(state.ui.organizer) "Cambiar a jugador" else "Cambiar a organizador",R.drawable.lh_users) { onEvent(SettingsEvent.Common(LocalEvent.ChangeMode)) }
   SettingsRow("Cambiar liga · ${state.league}",R.drawable.lh_league) { onEvent(SettingsEvent.Common(LocalEvent.Navigate(LocalSection.LEAGUES))) }
   if(state.ui.organizer) SettingsRow("Panel organizador",R.drawable.lh_trophy) { onEvent(SettingsEvent.Common(LocalEvent.OpenPage(LocalPage.DASHBOARD))) }
   SettingsRow("Respaldos",R.drawable.lh_shield) { onEvent(SettingsEvent.Common(LocalEvent.OpenBackups)) }
  } }
  item { OutlinedButton(onClick={onEvent(SettingsEvent.Common(LocalEvent.RequestLogout))},modifier=Modifier.fillMaxWidth()) { Text("Cerrar sesión",color=MaterialTheme.colorScheme.error) } }
 }
 if(state.privacy) AlertDialog(onDismissRequest={onEvent(SettingsEvent.Privacy(false))},title={Text("Privacidad y datos")},text={Text("Tus datos se guardan en Room/SQLite. Si conectas Firebase, la autenticación y las ligas se sincronizan con el proyecto configurado. Las ligas sincronizadas son visibles para usuarios registrados; solo su dueño puede editarlas. Las fotos se conservan en este teléfono. Las fotos elegidas usan accesos del selector de Android. Los respaldos no incluyen cuentas, contraseñas ni fotos. El anuncio remoto es una demostración de JSONPlaceholder; los datos deportivos no se envían a ese servicio.")},confirmButton={TextButton(onClick={onEvent(SettingsEvent.Privacy(false))}) { Text("Entendido") }})
}
@Composable private fun SettingsRow(label:String,icon:Int,onClick:()->Unit) {
 TextButton(onClick=onClick,modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {
  LeagueIcon(icon,null,tint=MaterialTheme.colorScheme.secondary);Spacer(Modifier.width(12.dp));Text(label,Modifier.weight(1f),color=MaterialTheme.colorScheme.onSurface);LeagueIcon(R.drawable.lh_chevron,null)
 }
 HorizontalDivider(color=MaterialTheme.colorScheme.outline)
}
