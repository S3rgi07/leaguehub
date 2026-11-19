package com.leaguehub.app.feature.local.center

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.data.repository.*
import com.leaguehub.app.feature.matches.model.MatchStatus
import com.leaguehub.app.feature.matches.matchcenter.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CenterViewModel(store:LocalHubViewModel,private val id:String,private val repository:MatchSheetRepository?,private val resolver:ContentResolver,private val saved:SavedStateHandle=SavedStateHandle()):LocalFeatureViewModel(store) {
 private data class Controls(val busy:Boolean=false,val error:String?=null,val message:String?=null,val editing:Boolean=false,val formation:String="2-2",val draft:List<String> = emptyList(),val outId:String="",val inId:String="",val minute:String="65",val duration:String="90",val clock:String="0")
 private val controls=MutableStateFlow(Controls())
 private val tab=saved.getStateFlow("tab",0);private val team=saved.getStateFlow("team","");private val court=saved.getStateFlow("court",130)
 @OptIn(ExperimentalCoroutinesApi::class)
 private val sheet=store.state.map { s->s.data.fixtures.find { it.id==id }?.let { f->s.data.leagues.find { it.id==f.leagueId }?.ownerId }.orEmpty() }.distinctUntilChanged().flatMapLatest { owner->repository?.observe(owner,id) ?: flowOf(MatchSheet()) }.catch { emit(MatchSheet());controls.update { it.copy(error="No se pudo leer el acta del partido.") } }
 private val selection=combine(tab,team,court) { t,c,m->Triple(t,c,m) }
 val state=combine(store.state,sheet,controls,selection) { original,document,c,selection ->
  val fixture=original.data.fixtures.find { it.id==id };val s=original.copy(leagueId=fixture?.leagueId)
  val selected=selection.second.takeIf { it==fixture?.homeId || it==fixture?.awayId } ?: fixture?.homeId.orEmpty()
  val roster=s.data.players.filter { it.clubId==selected }
  val courtIds=runCatching { MatchSheetRepository.playersOnCourt(document,selected) }.getOrDefault(emptyList())
  val lineups=fixture?.let { f->listOf(f.homeId,f.awayId).map { club ->
   val entry=document.teams.find { it.clubId==club };val ids=runCatching { MatchSheetRepository.playersOnCourt(document,club,selection.third) }.getOrDefault(emptyList())
   val players=s.data.players.filter { it.clubId==club };val points=positions(entry?.formation ?: "2-2")
   TeamLineupUiModel(s.teamModel(club),entry?.formation ?: "Por confirmar","Sin registrar",ids.mapIndexedNotNull { index,playerId->players.find { it.id==playerId }?.let { LineupPositionUiModel(s.playerModel(it),points[index].first,points[index].second) } },players.filter { it.id !in ids }.map { s.playerModel(it) })
  } }.orEmpty()
  val events=s.data.events.filter { it.fixtureId==id };fun count(kind:String,club:String)=events.count { it.kind==kind && s.data.players.any { p->p.id==it.playerId && p.clubId==club } }
  val model=fixture?.let { f->val base=s.matchModel(f);if(f.homeScore==null && document.phase!="SCHEDULED") base.copy(status=MatchStatus.LIVE,minute="${document.minute}′${if(document.phase=="HALFTIME") " · Descanso" else ""}",homeScore=count("GOAL",f.homeId),awayScore=count("GOAL",f.awayId)) else base }
  val stats=fixture?.let { f->listOf("GOAL" to "Goles registrados","ASSIST" to "Asistencias","YELLOW" to "Amarillas","RED" to "Rojas","APPEARANCE" to "Participaciones").map { (kind,label)->MatchStatisticUiModel(label,count(kind,f.homeId),count(kind,f.awayId)) } }.orEmpty()
  val timeline=events.filter { it.kind!="APPEARANCE" }.map { e->EventRow(e.id,"${e.minute}′ · ${eventTitle(e.kind)}",s.data.players.find { it.id==e.playerId }?.name.orEmpty(),null) }+document.substitutions.mapIndexed { i,sub->EventRow("sub-$i","${sub.minute}′ · Sustitución","${s.data.players.find { it.id==sub.outId }?.name} → ${s.data.players.find { it.id==sub.inId }?.name}",null) }
  CenterState(s.ui(),model,s.league?.name.orEmpty(),MatchCenterTab.entries.getOrElse(selection.first) { MatchCenterTab.SUMMARY },selected,lineups,stats,timeline.sortedByDescending { it.title.substringBefore('′').toIntOrNull() ?: 0 },document,roster.map { CourtPlayer(it.id,it.name,it.number,it.id in courtIds) },s.canManage,c.busy,c.error,c.message,c.editing,c.formation,c.draft,c.outId,c.inId,c.minute,c.duration,c.clock,selection.third,
   document.substitutions.filter { it.clubId==selected }.map { sub->"${sub.minute}′ · ${s.data.players.find { it.id==sub.outId }?.name} → ${s.data.players.find { it.id==sub.inId }?.name}" })
 }.stateIn(viewModelScope,SharingStarted.Eagerly,CenterState())
 private fun positions(formation:String):List<Pair<Float,Float>> {
  val lines=formation.split('-').map { it.toIntOrNull() ?: 0 };val rows=mutableListOf(.12f to .5f)
  lines.forEachIndexed { column,count->repeat(count) { index->rows+=((column+1f)/(lines.size+1f)*.82f+.08f) to ((index+1f)/(count+1f)) } }
  return rows
 }
 private fun action(work:suspend (MatchSheetRepository,String)->Unit) {
  if(!state.value.canManage || controls.value.busy) return
  val repo=repository ?: return
  val owner=store.state.value.session.userId ?: return
  controls.update { it.copy(busy=true,error=null,message=null) }
  viewModelScope.launch { try { work(repo,owner);controls.update { it.copy(message="Cambios guardados.") } } catch(c:CancellationException) { throw c } catch(e:Exception) { controls.update { it.copy(error=e.message ?: "No se pudo guardar el partido.") } } finally { controls.update { it.copy(busy=false) } } }
 }
 fun onEvent(e:CenterEvent) { when(e) {
  is CenterEvent.Common->common(e.event)
  is CenterEvent.Tab->saved["tab"]=e.tab.ordinal
  is CenterEvent.Team->{saved["team"]=e.id;controls.update { it.copy(outId="",inId="",editing=false) }}
  is CenterEvent.CourtMinute->saved["court"]=e.minute.coerceIn(0,130)
  is CenterEvent.Action->{ if(e.action==MatchAction.EVENTS) effect(LocalEffect.MatchEvents(id)) else matchAction(id,e.action) }
  CenterEvent.EditLineup->{val entry=state.value.sheet.teams.find { it.clubId==state.value.selectedTeam };controls.update { it.copy(editing=true,formation=entry?.formation ?: "2-2",draft=entry?.starters.orEmpty(),error=null) }}
  CenterEvent.CancelLineup->controls.update { it.copy(editing=false,error=null) }
  is CenterEvent.Formation->if(e.value in listOf("2-2","2-3-1","4-3-3")) controls.update { it.copy(formation=e.value) }
  is CenterEvent.TogglePlayer->if(state.value.players.any { it.id==e.id }) controls.update { it.copy(draft=if(e.id in it.draft) it.draft-e.id else it.draft+e.id) }
  CenterEvent.SaveLineup->{val selected=state.value.selectedTeam;val draft=controls.value;action { repo,owner->repo.update(owner,id) { current->require(current.substitutions.none { it.clubId==selected }) { "Deshaz los cambios de este equipo antes de modificar sus titulares" };current.copy(teams=current.teams.filter { it.clubId!=selected }+TeamSheet(selected,draft.formation,draft.draft)) };controls.update { it.copy(editing=false) } }}
  is CenterEvent.Field->controls.update { c->when(e.key) { "out"->c.copy(outId=e.value);"in"->c.copy(inId=e.value);"minute"->c.copy(minute=e.value);"duration"->c.copy(duration=e.value);"clock"->c.copy(clock=e.value);else->c } }
  CenterEvent.Substitute->{val c=controls.value;val selected=state.value.selectedTeam;action { repo,owner->val minute=requireNotNull(c.minute.toIntOrNull()) { "Ingresa el minuto del cambio" };repo.update(owner,id) { it.copy(substitutions=it.substitutions+Substitution(selected,c.outId,c.inId,minute)) } }}
  CenterEvent.UndoChange->{val selected=state.value.selectedTeam;action { repo,owner->repo.update(owner,id) { current->val index=current.substitutions.indexOfLast { it.clubId==selected };current.copy(substitutions=current.substitutions.filterIndexed { i,_->i!=index }) } }}
  CenterEvent.ApplyMinutes->{val duration=controls.value.duration;action { repo,owner->repo.applyMinutes(owner,id,requireNotNull(duration.toIntOrNull()) { "Duración inválida" }) }}
  is CenterEvent.Phase->action { repo,owner->repo.update(owner,id) { it.copy(phase=e.value) } }
  CenterEvent.SaveClock->{val minute=controls.value.clock;action { repo,owner->repo.update(owner,id) { it.copy(minute=requireNotNull(minute.toIntOrNull()) { "Minuto inválido" }) } }}
  CenterEvent.AddPhoto->if(state.value.canManage) effect(LocalEffect.ImageFile)
  is CenterEvent.PhotoPicked->e.uri?.let { uri->action { repo,owner->require(resolver.getType(uri)?.startsWith("image/")==true) { "Selecciona una imagen" };resolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);repo.update(owner,id) { it.copy(photos=(it.photos+uri.toString()).distinct()) } } }
  is CenterEvent.RemovePhoto->action { repo,owner->repo.update(owner,id) { it.copy(photos=it.photos-e.uri) } }
 } }
}
