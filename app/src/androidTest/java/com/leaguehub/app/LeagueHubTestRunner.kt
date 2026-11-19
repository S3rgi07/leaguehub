package com.leaguehub.app
import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
class LeagueHubTestApplication:LeagueHubApplication() { override val firebaseEnabled:Boolean get()=false }
/** Ordinary device tests never create accounts or change data in the production Firebase project. */
class LeagueHubTestRunner:AndroidJUnitRunner() {
 override fun newApplication(cl:ClassLoader,className:String,context:Context):Application=super.newApplication(cl,LeagueHubTestApplication::class.java.name,context)
}
