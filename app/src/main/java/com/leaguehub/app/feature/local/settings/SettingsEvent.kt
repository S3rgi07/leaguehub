package com.leaguehub.app.feature.local.settings
import com.leaguehub.app.feature.local.*

sealed interface SettingsEvent {
 data class Common(val event:LocalEvent):SettingsEvent
 data object EditName:SettingsEvent
 data object Password:SettingsEvent
 data class Privacy(val visible:Boolean):SettingsEvent
}
