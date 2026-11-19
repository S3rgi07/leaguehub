package com.leaguehub.app.feature.local.cloud
import android.content.ContentResolver
import com.leaguehub.app.data.cloud.*
import com.leaguehub.app.data.repository.AuthRepository
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
class CloudViewModel(store:LocalHubViewModel,private val engine:CloudSyncEngine,private val auth:AuthRepository,private val resolver:ContentResolver):LocalFeatureViewModel(store) {
 private val controls=MutableStateFlow(CloudState())
 val state=combine(store.state,engine.status,controls,auth.cloudNotice) { s,cloud,c,notice->c.copy(ui=s.ui(),sync=cloud,error=c.error ?: notice) }.stateIn(viewModelScope,SharingStarted.Eagerly,CloudState())
 private fun work(block:suspend ()->Unit) {
  if(controls.value.busy) return
  controls.update { it.copy(busy=true,error=null,message=null) }
  viewModelScope.launch { try { block() } catch(e:Exception) { if(e is CancellationException) throw e;controls.update { it.copy(error=e.message ?: "No se pudo completar la sincronización.") } } finally { controls.update { it.copy(busy=false,password="") } } }
 }
 fun onEvent(e:CloudEvent) { when(e) {
  is CloudEvent.Common->common(e.event)
  is CloudEvent.Password->controls.update { it.copy(password=e.value,error=null) }
  CloudEvent.Connect->{val password=controls.value.password;work { auth.connectCurrent(password);engine.retry();controls.update { it.copy(message="Cuenta conectada. La sincronización comenzará al recibir la nube.") } }}
  CloudEvent.Retry->engine.retry()
  is CloudEvent.AskResolve->controls.update { it.copy(confirmation=e.resolution) }
  CloudEvent.ConfirmResolve->{val resolution=controls.value.confirmation ?: return;work { engine.resolve(resolution.id,resolution.keepLocal);controls.update { it.copy(confirmation=null,message="Versión seleccionada. Se guardó una copia de recuperación de la otra versión.") } }}
  CloudEvent.ExportRecovery->effect(LocalEffect.ExportFile("LeagueHub-recuperacion.json"))
  is CloudEvent.ExportPicked->e.uri?.let { uri->work { withContext(Dispatchers.IO) { val text=engine.exportRecoveries();requireNotNull(resolver.openOutputStream(uri,"wt")) { "No se pudo abrir el archivo" }.bufferedWriter(Charsets.UTF_8).use { it.write(text) } };controls.update { it.copy(message="Copia exportada. Puedes importarla desde Respaldos.") } } }
 } }
}
