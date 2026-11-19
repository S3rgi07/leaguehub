package com.leaguehub.app.feature.local.matchdetail
import com.leaguehub.app.feature.local.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.leaguehub.app.core.components.*
import com.leaguehub.app.R
@Composable fun MatchDetailScreen(state:MatchDetailState,onEvent:(MatchDetailEvent)->Unit) {
 val match=state.match
 if(match==null) { LocalFrame(state.ui,LocalSection.MATCHES,{onEvent(MatchDetailEvent.Common(it))},showBack=true,showLeague=false,title="Detalle del partido") { item { EmptyLocal("Partido no disponible.") } };return }
 com.leaguehub.app.feature.matches.detail.MatchDetailScreen(match,{onEvent(MatchDetailEvent.Common(LocalEvent.Back))},{onEvent(MatchDetailEvent.Map)},{onEvent(MatchDetailEvent.Map)},{onEvent(MatchDetailEvent.Share)},{onEvent(MatchDetailEvent.Center)},{onEvent(MatchDetailEvent.Common(LocalEvent.Navigate(it.localSection())))})
}
