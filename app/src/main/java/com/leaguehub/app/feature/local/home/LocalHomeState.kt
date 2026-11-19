package com.leaguehub.app.feature.local.home
import com.leaguehub.app.feature.local.*
data class RemoteAnnouncementState(val loading:Boolean=false,val text:String?=null,val error:String?=null)
data class LocalHomeState(val ui:LocalUiState=LocalUiState(),val leagueName:String?=null,val summary:String="",val results:String="",val upcoming:List<MatchRow> = emptyList(),val canManage:Boolean=false,val announcement:RemoteAnnouncementState=RemoteAnnouncementState(),val recent:List<MatchRow> = emptyList())
