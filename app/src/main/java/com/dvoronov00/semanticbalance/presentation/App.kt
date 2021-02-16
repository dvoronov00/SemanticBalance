package com.dvoronov00.semanticbalance.presentation

import android.app.Application
import android.util.Log
import com.dvoronov00.semanticbalance.presentation.di.AppComponent
import com.dvoronov00.semanticbalance.presentation.di.DaggerAppComponent
import com.dvoronov00.semanticbalance.presentation.di.module.ContextModule
import io.reactivex.rxjava3.plugins.RxJavaPlugins

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

    }
}