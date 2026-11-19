package com.leaguehub.app.feature.local.settings
import com.leaguehub.app.feature.local.*

data class SettingsState(val ui:LocalUiState=LocalUiState(),val league:String="",val privacy:Boolean=false)
