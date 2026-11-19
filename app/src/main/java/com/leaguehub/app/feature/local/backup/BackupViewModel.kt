package com.leaguehub.app.feature.local.backup
import com.leaguehub.app.feature.local.*

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class BackupViewModel(private val repository: BackupRepository,private val sessions: UserSessionRepository,private val resolver: ContentResolver,store:LocalHubViewModel): LocalFeatureViewModel(store) {
    private val mutable=MutableStateFlow(BackupState())
    val state=mutable.asStateFlow()
    private var previewOwner: String?=null
    init { viewModelScope.launch { store.state.collect { value -> mutable.update { it.copy(ui=value.ui()) } } } }
    fun onEvent(event:BackupEvent) {
        if(mutable.value.busy) return
        when(event) {
            is BackupEvent.Common -> common(event.event)
            BackupEvent.ChooseExport -> effect(LocalEffect.ExportFile("LeagueHub-${java.time.LocalDate.now()}.json"))
            BackupEvent.ChooseImport -> effect(LocalEffect.ImportFile)
            is BackupEvent.ExportSelected -> export(event.uri)
            is BackupEvent.ImportSelected -> inspect(event.uri)
            BackupEvent.Confirm -> confirm()
            BackupEvent.Dismiss -> dismiss()
        }
    }
    private suspend fun owner(): String {
        sessions.restoreCompleted.first { it }
        val s=sessions.session.value
        require(s.isAuthenticated && s.role==SessionRole.ORGANIZER) { "Cambia al modo organizador para usar respaldos" }
        return requireNotNull(s.userId) { "Inicia sesión de nuevo" }
    }
    private fun run(action: suspend () -> Unit) {
        if(mutable.value.busy) return
        mutable.update { it.copy(busy=true,error=null,message=null) }
        viewModelScope.launch {
            try { action();mutable.update { it.copy(busy=false) } }
            catch(c: CancellationException) { throw c }
            catch(e: Exception) { mutable.update { it.copy(busy=false,error=if(e is IllegalArgumentException) e.message else "No se pudo leer o guardar el respaldo. Revisa el archivo y su ubicación.") } }
        }
    }
    private fun export(uri: Uri?) { if(uri==null) return;run {
        val id=owner()
        val content=repository.export(id)
        withContext(Dispatchers.IO) {
            requireNotNull(resolver.openOutputStream(uri,"wt")) { "No se pudo abrir el archivo de destino" }.use { it.write(content.toByteArray(Charsets.UTF_8));it.flush() }
        }
        mutable.update { it.copy(message="Respaldo exportado correctamente") }
    } }
    private fun inspect(uri: Uri?) { if(uri==null) return;run {
        val id=owner()
        val document=withContext(Dispatchers.IO) {
            requireNotNull(resolver.openInputStream(uri)) { "No se pudo abrir el archivo seleccionado" }.use { repository.read(it) }
        }
        require(id==owner()) { "La sesión cambió. Selecciona otra vez el archivo." }
        previewOwner=id
        mutable.update { it.copy(preview=document) }
    } }
    private fun confirm() { val document=mutable.value.preview ?: return;run {
        val id=owner();require(id==previewOwner) { "La sesión cambió. Selecciona otra vez el archivo." }
        val count=repository.restore(id,document)
        previewOwner=null
        mutable.update { it.copy(preview=null,message="Se importaron $count ligas como copias nuevas") }
    } }
    private fun dismiss() { if(!mutable.value.busy) { previewOwner=null;mutable.update { BackupState(ui=it.ui) } } }
}
