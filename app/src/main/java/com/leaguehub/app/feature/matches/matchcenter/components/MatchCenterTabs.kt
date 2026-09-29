package com.leaguehub.app.feature.matches.matchcenter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.leaguehub.app.feature.matches.matchcenter.model.MatchCenterTab

@Composable
fun MatchCenterTabs(selected: MatchCenterTab, onSelected: (MatchCenterTab) -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().selectableGroup()) {
            MatchCenterTab.entries.forEach { tab ->
                val active = tab == selected
                Column(Modifier.weight(1f).selectable(active, role = Role.Tab, onClick = { onSelected(tab) }),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(tab.label, Modifier.padding(vertical = 16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
                    Box(Modifier.fillMaxWidth().height(2.dp).background(
                        if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface))
                }
            }
        }
    }
}
