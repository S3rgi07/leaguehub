package com.leaguehub.app.feature.local

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.data.session.SessionRole
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class BackupState(val busy: Boolean=false,val preview: BackupDocument?=null,val error: String?=null,val message: String?=null)
class BackupViewModel(private val repository: BackupRepository,private val sessions: UserSessionRepository,private val resolver: ContentResolver): ViewModel() {
    private val mutable=MutableStateFlow(BackupState())
    val state=mutable.asStateFlow()
    private var previewOwner: String?=null
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
    fun export(uri: Uri?) { if(uri==null) return;run {
        val id=owner()
        val content=repository.export(id)
        withContext(Dispatchers.IO) {
            requireNotNull(resolver.openOutputStream(uri,"wt")) { "No se pudo abrir el archivo de destino" }.use { it.write(content.toByteArray(Charsets.UTF_8));it.flush() }
        }
        mutable.update { it.copy(message="Respaldo exportado correctamente") }
    } }
    fun inspect(uri: Uri?) { if(uri==null) return;run {
        val id=owner()
        val document=withContext(Dispatchers.IO) {
            requireNotNull(resolver.openInputStream(uri)) { "No se pudo abrir el archivo seleccionado" }.use { repository.read(it) }
        }
        require(id==owner()) { "La sesión cambió. Selecciona otra vez el archivo." }
        previewOwner=id
        mutable.update { it.copy(preview=document) }
    } }
    fun confirm() { val document=mutable.value.preview ?: return;run {
        val id=owner();require(id==previewOwner) { "La sesión cambió. Selecciona otra vez el archivo." }
        val count=repository.restore(id,document)
        previewOwner=null
        mutable.update { it.copy(preview=null,message="Se importaron $count ligas como copias nuevas") }
    } }
    fun dismiss() { if(!mutable.value.busy) { previewOwner=null;mutable.value=BackupState() } }
}
