package com.dvoronov00.semanticbalance.presentation

import android.app.Application
import android.util.Log
import com.dvoronov00.semanticbalance.BuildConfig
import com.dvoronov00.semanticbalance.presentation.di.AppComponent
import com.dvoronov00.semanticbalance.presentation.di.DaggerAppComponent
import com.dvoronov00.semanticbalance.presentation.di.module.ContextModule
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber

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

        // Creating an extended library configuration.
        val config: YandexMetricaConfig = YandexMetricaConfig.newConfigBuilder(APP_METRICA_KEY).build()
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
