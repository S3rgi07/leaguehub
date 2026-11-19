package com.leaguehub.app.feature.local.backup
import android.net.Uri
import com.leaguehub.app.feature.local.LocalEvent
sealed interface BackupEvent {
 data class Common(val event:LocalEvent):BackupEvent
 data object ChooseExport:BackupEvent
 data object ChooseImport:BackupEvent
 data class ExportSelected(val uri:Uri?):BackupEvent
 data class ImportSelected(val uri:Uri?):BackupEvent
 data object Confirm:BackupEvent
 data object Dismiss:BackupEvent
}
