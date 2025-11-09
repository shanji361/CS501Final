package com.example.weatherapi

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

//initializing facebook sdk
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}
