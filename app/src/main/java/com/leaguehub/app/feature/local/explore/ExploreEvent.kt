package com.leaguehub.app.feature.local.explore
import com.leaguehub.app.feature.local.*

sealed interface ExploreEvent { data class Common(val event:LocalEvent):ExploreEvent;data class Query(val value:String):ExploreEvent;data class Open(val id:String):ExploreEvent }
