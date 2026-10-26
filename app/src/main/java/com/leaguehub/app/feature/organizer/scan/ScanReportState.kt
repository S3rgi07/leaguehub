package com.leaguehub.app.feature.organizer.scan

import com.leaguehub.app.data.fake.falconsVsTitans
import com.leaguehub.app.feature.matches.model.MatchUiModel
import com.leaguehub.app.feature.organizer.model.ScanStatus

data class ScanReportState(
    val match: MatchUiModel = falconsVsTitans,
    val status: ScanStatus = ScanStatus.DETECTED,
    val homeScore: Int = 2,
    val awayScore: Int = 1,
    val isProcessed: Boolean = false
)
