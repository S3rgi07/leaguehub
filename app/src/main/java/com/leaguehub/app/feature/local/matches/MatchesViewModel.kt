package com.leaguehub.app.feature.local.matches
import com.leaguehub.app.feature.local.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
class MatchesViewModel(store:LocalHubViewModel):LocalFeatureViewModel(store) {
    private data class Selection(val month:java.time.YearMonth=java.time.YearMonth.now(),val day:java.time.LocalDate?=null,val filtered:Boolean=false)
    private val selection=MutableStateFlow(Selection())
    val state=combine(store.state,selection) { s,c ->
        val matches=s.fixtures.sortedBy { it.kickoff }.map { s.matchRow(it) }
        val days=com.leaguehub.app.feature.matches.calendar.calendarDays(c.month).map { d->CalendarDay(d,java.time.YearMonth.from(d)==c.month,matches.any { it.day==d }) }
        MatchesState(s.ui(),matches.filter { !c.filtered || if(c.day!=null) it.day==c.day else it.day?.let(java.time.YearMonth::from)==c.month },s.canManage,s.canManage && s.clubs.size>=2,s.canManage && s.clubs.size<2,c.month,c.day,days,c.filtered)
    }.stateIn(viewModelScope,SharingStarted.Eagerly,MatchesState())
    fun onEvent(event:MatchesEvent) { when(event) {
        is MatchesEvent.Common -> common(event.event)
        is MatchesEvent.Month -> selection.update { it.copy(month=it.month.plusMonths(event.delta),day=null,filtered=true) }
        is MatchesEvent.Day -> selection.update { it.copy(month=java.time.YearMonth.from(event.date),day=event.date,filtered=true) }
        MatchesEvent.ShowAll -> selection.update { it.copy(day=null,filtered=false) }
        MatchesEvent.Add -> store.onEvent(LocalEvent.Edit("fixture"))
            is MatchesEvent.Action -> matchAction(event.id,event.action)
    } }
}
