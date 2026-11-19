package com.leaguehub.app.feature.local.teamdetail
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class TeamDetailViewModel(store:LocalHubViewModel,private val id:String):LocalFeatureViewModel(store) {
 private val full=MutableStateFlow(false);private val player=MutableStateFlow<String?>(null)
 val state=combine(store.state,full,player) { s,expanded,selected->
  val team=s.data.clubs.find { it.id==id };val table=team?.let { s.data.table(it.leagueId) }.orEmpty();val position=table.indexOfFirst { it.club.id==id };val row=table.getOrNull(position)
  val players=s.data.players.filter { it.clubId==id }.map { s.playerModel(it) }
  TeamDetailState(s.ui(),team?.let { s.teamModel(id) },row?.let { com.leaguehub.app.feature.league.model.StandingUiModel(position+1,s.teamModel(id),it.played,it.difference,it.points) },s.data.fixtures.filter { it.homeScore==null && (it.homeId==id || it.awayId==id) }.minByOrNull { it.kickoff }?.let { s.matchModel(it) },players,id in s.followedClubIds,expanded,players.find { it.id==selected })
 }.stateIn(viewModelScope,SharingStarted.Eagerly,TeamDetailState())
 fun onEvent(e:TeamDetailEvent) { when(e) { is TeamDetailEvent.Common->common(e.event);TeamDetailEvent.Follow->store.onEvent(LocalEvent.Follow(id));TeamDetailEvent.Full->full.update { !it };is TeamDetailEvent.Player->player.value=e.id;is TeamDetailEvent.Match->effect(LocalEffect.Details(e.id));TeamDetailEvent.Share->effect(LocalEffect.Share("${state.value.team?.name} · ${state.value.players.size} jugadores · LeagueHub")) } }
}
