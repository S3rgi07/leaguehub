package com.leaguehub.app.feature.local.matches
import com.leaguehub.app.feature.local.*
data class MatchesState(val ui:LocalUiState=LocalUiState(),val matches:List<MatchRow> = emptyList(),val canManage:Boolean=false,val canCreate:Boolean=false,val needTeams:Boolean=false,val month:java.time.YearMonth=java.time.YearMonth.now(),val selectedDate:java.time.LocalDate?=null,val days:List<CalendarDay> = emptyList(),val filtered:Boolean=false)
data class CalendarDay(val date:java.time.LocalDate,val inMonth:Boolean,val hasMatch:Boolean)
