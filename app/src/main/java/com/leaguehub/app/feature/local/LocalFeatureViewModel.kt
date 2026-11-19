package com.leaguehub.app.feature.local
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface LocalEffect {
    data class Page(val page:LocalPage):LocalEffect
    data class League(val id:String):LocalEffect
    data class Team(val id:String):LocalEffect
    data class Center(val id:String):LocalEffect
    data class Details(val id:String):LocalEffect
    data object ImageFile:LocalEffect
    data class Section(val section:LocalSection):LocalEffect
    data class ClubPlayers(val clubId:String):LocalEffect
    data class MatchEvents(val matchId:String):LocalEffect
    data class Location(val query:String):LocalEffect
    data class Share(val text:String):LocalEffect
    data object Back:LocalEffect
    data class ExportFile(val fileName:String):LocalEffect
    data object ImportFile:LocalEffect
    data object Backups:LocalEffect
}
/** Shared form commands remain in the graph-scoped store; each feature owns its intents and projection. */
abstract class LocalFeatureViewModel(protected val store:LocalHubViewModel):ViewModel() {
    private val channel=Channel<LocalEffect>(Channel.BUFFERED)
    val effects=channel.receiveAsFlow()
    protected fun effect(effect:LocalEffect) { viewModelScope.launch { channel.send(effect) } }
    protected fun common(event:LocalEvent) {
        when(event) {
            is LocalEvent.Navigate -> if(!store.state.value.busy) { store.onEvent(LocalEvent.Dismiss);effect(LocalEffect.Section(event.section)) }
            LocalEvent.Back -> if(!store.state.value.busy) effect(LocalEffect.Back)
            is LocalEvent.OpenPage -> effect(LocalEffect.Page(event.page))
            LocalEvent.OpenBackups -> effect(LocalEffect.Backups)
            else -> store.onEvent(event)
        }
    }
    protected fun matchAction(id:String,share:Boolean) {
        val match=store.state.value.data.fixtures.find { it.id==id } ?: return
        val row=store.state.value.matchRow(match)
        effect(if(share) LocalEffect.Share(row.shareText) else LocalEffect.Location(row.venue))
    }
    protected fun matchAction(id:String,action:MatchAction) {
        val item=store.state.value.data.fixtures.find { it.id==id } ?: return
        when(action) {
            MatchAction.CENTER -> effect(LocalEffect.Center(id))
            MatchAction.DETAIL -> effect(LocalEffect.Details(id))
            MatchAction.EVENTS -> effect(LocalEffect.MatchEvents(id))
            MatchAction.MAP -> matchAction(id,false)
            MatchAction.SHARE -> matchAction(id,true)
            MatchAction.EDIT -> store.onEvent(LocalEvent.Edit("fixture",id))
            MatchAction.RESULT -> store.onEvent(LocalEvent.Edit("result",id))
            MatchAction.REOPEN -> store.onEvent(LocalEvent.ReopenResult(id))
            MatchAction.DELETE -> store.onEvent(LocalEvent.RequestDelete("fixture",id,store.state.value.matchRow(item).title))
        }
    }
}
