package com.leaguehub.app.feature.local.cloud
import com.leaguehub.app.feature.local.*
import com.leaguehub.app.data.cloud.CloudSyncStatus
data class CloudState(val ui:LocalUiState=LocalUiState(),val sync:CloudSyncStatus=CloudSyncStatus(),val password:String="",val busy:Boolean=false,val error:String?=null,val message:String?=null,val confirmation:Resolution?=null)
data class Resolution(val id:String,val keepLocal:Boolean)
