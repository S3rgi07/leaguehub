package com.leaguehub.app.feature.organizer.scan

import androidx.compose.runtime.Composable
import com.leaguehub.app.data.fake.falconsVsTitans
import com.leaguehub.app.feature.organizer.model.ScanStatus

@Composable
fun ScanReportRoute() {
    ScanReportScreen(falconsVsTitans, ScanStatus.DETECTED,
        falconsVsTitans.homeScore ?: 0, falconsVsTitans.awayScore ?: 0)
}
