package com.leaguehub.app.feature.local.cloud
import android.net.Uri
import com.leaguehub.app.feature.local.LocalEvent
sealed interface CloudEvent {
 data class Common(val event:LocalEvent):CloudEvent
 data class Password(val value:String):CloudEvent
 data object Connect:CloudEvent
 data object Retry:CloudEvent
 data class AskResolve(val resolution:Resolution?):CloudEvent
 data object ConfirmResolve:CloudEvent
 data class ExportRecovery(val id:String?=null):CloudEvent
 data class ExportPicked(val uri:Uri?):CloudEvent
}
