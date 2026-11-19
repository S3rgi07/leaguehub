package com.leaguehub.app.feature.local.matchdetail
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class MatchDetailViewModel(store:LocalHubViewModel,private val id:String):LocalFeatureViewModel(store) {
 val state=store.state.map { s->MatchDetailState(s.ui(),s.data.fixtures.find { it.id==id }?.let { s.matchModel(it) }) }.stateIn(viewModelScope,SharingStarted.Eagerly,MatchDetailState())
 fun onEvent(e:MatchDetailEvent) { when(e) { is MatchDetailEvent.Common->common(e.event);MatchDetailEvent.Map->matchAction(id,false);MatchDetailEvent.Share->matchAction(id,true);MatchDetailEvent.Center->effect(LocalEffect.Center(id)) } }
}
