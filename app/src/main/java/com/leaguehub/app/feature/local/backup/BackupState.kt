package com.leaguehub.app.feature.local.backup
import com.leaguehub.app.feature.local.LocalUiState
import com.leaguehub.app.data.repository.BackupDocument
data class BackupState(val ui:LocalUiState=LocalUiState(),val busy:Boolean=false,val preview:BackupDocument?=null,val error:String?=null,val message:String?=null)
