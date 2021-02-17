package com.dvoronov00.semanticbalance.presentation

import android.app.Application
import android.util.Log
import com.dvoronov00.semanticbalance.presentation.di.AppComponent
import com.dvoronov00.semanticbalance.presentation.di.DaggerAppComponent
import com.dvoronov00.semanticbalance.presentation.di.module.ContextModule
import com.onesignal.OneSignal
import io.reactivex.rxjava3.plugins.RxJavaPlugins


const val ONESIGNAL_APP_ID = "46d16877-69fd-403b-b5ec-01263903458f"

class App : Application() {

    companion object{
        private lateinit var component: AppComponent
        fun getComponent() = component
    }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("App", throwable.toString()) }

        component = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}