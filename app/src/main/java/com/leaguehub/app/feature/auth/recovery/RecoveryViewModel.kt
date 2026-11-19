package com.leaguehub.app.feature.auth.recovery
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class RecoveryViewModel(private val auth:AuthRepository?=null):ViewModel() {
 private val mutable=MutableStateFlow(RecoveryState(enabled=auth?.cloudConfigured==true))
 val state=mutable.asStateFlow()
 private val channel=Channel<Unit>(Channel.BUFFERED)
 val back=channel.receiveAsFlow()
 fun onEvent(event:RecoveryEvent) { when(event) {
  RecoveryEvent.BackClicked->viewModelScope.launch { channel.send(Unit) }
  is RecoveryEvent.EmailChanged->mutable.update { it.copy(email=event.value,error=null,message=null) }
  RecoveryEvent.SendClicked->{val s=state.value;if(s.loading || !s.enabled) return
   if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) { mutable.update { it.copy(error="Ingresa un correo válido") };return }
   mutable.update { it.copy(loading=true,error=null,message=null) }
   viewModelScope.launch { try { requireNotNull(auth).resetPassword(s.email);mutable.update { it.copy(message="Si el correo corresponde a una cuenta de Firebase, recibirás un enlace para restablecer tu contraseña.") } } catch(e:Exception) { if(e is kotlinx.coroutines.CancellationException) throw e;mutable.update { it.copy(error=e.message ?: "No se pudo solicitar la recuperación") } } finally { mutable.update { it.copy(loading=false) } } }
  }
 } }
}
