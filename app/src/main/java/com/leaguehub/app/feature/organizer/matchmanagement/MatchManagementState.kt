package com.leaguehub.app.feature.organizer.matchmanagement

import com.leaguehub.app.data.fake.fakeMatchManagement
import com.leaguehub.app.feature.organizer.model.MatchManagementUiModel

data class MatchManagementState(
    val data: MatchManagementUiModel = fakeMatchManagement,
    val selectedRosterId: String? = null,
    val errorMessage: String? = null
)
