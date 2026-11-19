package com.leaguehub.app.feature.local
enum class LocalPage { CLOUD, SETTINGS, ALERTS, EXPLORE, DASHBOARD }
sealed interface LocalEvent {
    data class OpenPage(val page:LocalPage):LocalEvent
    data class Navigate(val section: LocalSection): LocalEvent
    data object Back: LocalEvent
    data object OpenBackups: LocalEvent
    data object Retry: LocalEvent
    data class SelectLeague(val id: String): LocalEvent
    data class SelectClub(val id: String?): LocalEvent
    data class SelectFixture(val id: String?): LocalEvent
    data class Follow(val id: String): LocalEvent
    data class Edit(val kind: String,val id: String?=null): LocalEvent
    data class RequestDelete(val kind: String,val id: String,val label: String): LocalEvent
    data class FieldChanged(val key: String,val value: String): LocalEvent
    data class ReopenResult(val id: String): LocalEvent
    data object Save: LocalEvent
    data object Dismiss: LocalEvent
    data object ConfirmDelete: LocalEvent
    data object ChangeMode: LocalEvent
    data object ChangeAppearance: LocalEvent
    data object RequestLogout: LocalEvent
    data object CancelLogout: LocalEvent
    data object ConfirmLogout: LocalEvent
}
