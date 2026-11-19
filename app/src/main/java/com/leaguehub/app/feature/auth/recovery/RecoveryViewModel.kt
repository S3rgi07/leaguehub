package com.leaguehub.app.feature.auth.recovery
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class RecoveryViewModel:ViewModel() {
 private val mutable=MutableStateFlow(RecoveryState())
 val state=mutable.asStateFlow()
 private val channel=Channel<Unit>(Channel.BUFFERED)
 val back=channel.receiveAsFlow()
 fun onEvent(event:RecoveryEvent) { when(event) { RecoveryEvent.BackClicked->viewModelScope.launch { channel.send(Unit) } } }
}
