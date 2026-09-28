package com.leaguehub.app.feature.matches.matchcenter

import androidx.compose.runtime.Composable
import com.leaguehub.app.data.fake.*
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab

/** Static data wiring for the visual delivery. Navigation and live updates come later. */
@Composable
fun MatchCenterRoute(tab: MatchCenterTab = MatchCenterTab.SUMMARY) {
    MatchCenterScreen(falconsVsTitans, universityLeague.name, tab, fakeMatchEvents,
        fakeLineups, falcons.id, fakeMatchStatistics, fakeMomentum)
}
