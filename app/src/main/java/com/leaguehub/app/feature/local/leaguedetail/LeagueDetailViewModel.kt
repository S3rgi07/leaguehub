package com.leaguehub.app.feature.local.leaguedetail
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class LeagueDetailViewModel(store:LocalHubViewModel,private val id:String):LocalFeatureViewModel(store) {
 private val tab=MutableStateFlow(com.leaguehub.app.feature.league.detail.LeagueTab.OVERVIEW)
 val state=combine(store.state,tab) { original,t->
  val s=original.copy(leagueId=id);val league=s.league
  LeagueDetailState(s.ui(),league?.let { com.leaguehub.app.feature.league.model.LeagueUiModel(it.id,it.name,it.season,s.fixtures.filter { f->f.homeScore!=null }.maxOfOrNull { f->f.matchday } ?: 0,s.fixtures.maxOfOrNull { f->f.matchday } ?: 0,s.clubs.size,s.players.size,s.fixtures.size,s.fixtures.any { f->f.homeScore==null }) },s.clubs.map { s.teamModel(it.id) },s.fixtures.filter { it.homeScore!=null }.sortedByDescending { it.kickoff }.map { f->val row=s.matchRow(f);com.leaguehub.app.feature.league.model.LeagueNewsUiModel(f.id,row.title,row.score,row.shareText) },t)
 }.stateIn(viewModelScope,SharingStarted.Eagerly,LeagueDetailState())
 fun onEvent(e:LeagueDetailEvent) { when(e) { is LeagueDetailEvent.Common->common(e.event);is LeagueDetailEvent.Tab->tab.value=e.tab;is LeagueDetailEvent.Team->effect(LocalEffect.Team(e.id));is LeagueDetailEvent.News->effect(LocalEffect.Center(e.id)) } }
}
