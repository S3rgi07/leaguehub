package com.leaguehub.app.feature.auth.recovery
sealed interface RecoveryEvent { data object BackClicked:RecoveryEvent;data class EmailChanged(val value:String):RecoveryEvent;data object SendClicked:RecoveryEvent }
