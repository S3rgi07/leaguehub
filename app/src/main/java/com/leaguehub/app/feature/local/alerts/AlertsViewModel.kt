package com.leaguehub.app.feature.local.alerts
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
class AlertsViewModel(store:LocalHubViewModel,private val preferences:com.leaguehub.app.data.local.UserDataDao?):LocalFeatureViewModel(store) {
 private val filter=MutableStateFlow(0);private val error=MutableStateFlow<String?>(null)
 private val json=kotlinx.serialization.json.Json
 @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
 private val read=store.state.map { it.session.userId.orEmpty() }.distinctUntilChanged().flatMapLatest { user->preferences?.observe(user,"read_alerts")?.map { text->runCatching { json.decodeFromString<List<String>>(text ?: "[]").toSet() }.getOrDefault(emptySet()) } ?: flowOf(emptySet()) }
 private var all:List<AlertRow> = emptyList();private var writing=false
 val state=combine(store.state,read,filter,error) { s,seen,f,e->
  val leagueIds=s.data.clubs.filter { it.id in s.followedClubIds }.map { it.leagueId }.toSet()+listOfNotNull(s.leagueId)
  val rows=s.data.fixtures.filter { it.leagueId in leagueIds }.sortedByDescending { it.kickoff }.take(80).map { match->
   val row=s.matchRow(match);val id="${match.id}:${match.homeScore}:${match.awayScore}"
   AlertRow(id,if(match.homeScore==null) "Próximo partido" else "Resultado publicado",row.shareText,match.id,id !in seen,match.leagueId)
  }+s.data.leagues.filter { it.id in leagueIds }.map { league->val count=s.data.fixtures.count { it.leagueId==league.id && it.homeScore!=null };val id="table:${league.id}:$count";AlertRow(id,"Tabla de ${league.name}","$count resultados registrados. Consulta la clasificación actualizada.",null,id !in seen,league.id) }
  all=rows
  AlertsState(s.ui(),rows.filter { f==0 || (f==1 && it.matchId!=null) || (f==2 && it.matchId==null) },f,rows.count { it.unread },e)
 }.stateIn(viewModelScope,SharingStarted.Eagerly,AlertsState())
 private fun mark(ids:Set<String>) { if(writing) return;val user=store.state.value.session.userId ?: return;val dao=preferences ?: return;writing=true
  viewModelScope.launch { try { val old=runCatching { json.decodeFromString<List<String>>(dao.read(user,"read_alerts") ?: "[]") }.getOrDefault(emptyList());dao.upsert(com.leaguehub.app.data.local.UserDataEntity(user,"read_alerts",json.encodeToString((old+ids).distinct().takeLast(2000)))) } catch(c:kotlinx.coroutines.CancellationException) { throw c } catch(_:Exception) { error.value="No se pudo guardar el estado de lectura." } finally { writing=false } }
 }
 fun onEvent(e:AlertsEvent) { when(e) { is AlertsEvent.Common->common(e.event);is AlertsEvent.Filter->if(e.index in 0..2) filter.value=e.index;AlertsEvent.MarkAll->mark(all.map { it.id }.toSet());is AlertsEvent.Open->{ val row=all.find { it.id==e.id } ?: return;mark(setOf(row.id));effect(row.matchId?.let { LocalEffect.Center(it) } ?: LocalEffect.League(row.leagueId)) } } }
}
