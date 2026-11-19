package com.leaguehub.app.feature.auth.recovery
sealed interface RecoveryEvent { data object BackClicked:RecoveryEvent }
