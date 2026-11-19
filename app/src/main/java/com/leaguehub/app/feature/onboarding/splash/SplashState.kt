package com.leaguehub.app.feature.onboarding.splash
enum class SplashTarget { ONBOARDING, ROLE, LOCAL }
data class SplashState(val isLoading:Boolean=true,val isReady:Boolean=false,val target:SplashTarget?=null,val message:String?=null)
