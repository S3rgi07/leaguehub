package com.leaguehub.app.feature.organizer.scan

sealed interface ScanReportEvent {
    data class HomeScoreChanged(val score: Int) : ScanReportEvent
    data class AwayScoreChanged(val score: Int) : ScanReportEvent
    data object RetryClicked : ScanReportEvent
    data object ProcessClicked : ScanReportEvent
}
