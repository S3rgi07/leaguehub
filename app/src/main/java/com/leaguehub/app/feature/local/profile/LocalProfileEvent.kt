package com.leaguehub.app.feature.local.profile
import com.leaguehub.app.feature.local.*
sealed interface LocalProfileEvent {
    data class Common(val event:LocalEvent):LocalProfileEvent
    data object EditName:LocalProfileEvent
    data object Password:LocalProfileEvent
    data class OpenClub(val id:String):LocalProfileEvent
}
