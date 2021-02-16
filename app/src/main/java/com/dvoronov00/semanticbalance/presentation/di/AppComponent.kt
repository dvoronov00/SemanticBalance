package com.dvoronov00.semanticbalance.presentation.di

import android.content.Context
import com.dvoronov00.semanticbalance.presentation.di.module.*
import com.dvoronov00.semanticbalance.presentation.ui.MainActivity
import com.dvoronov00.semanticbalance.presentation.ui.account.AccountFragment
import com.dvoronov00.semanticbalance.presentation.ui.auth.AuthActivity
import com.dvoronov00.semanticbalance.presentation.ui.reports.ReportsFragment
import com.dvoronov00.semanticbalance.presentation.ui.splash.SplashActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ContextModule::class,
        NetworkModule::class,
        ViewModelModule::class,
        RepositoryModule::class,
        NavigationModule::class,
        DataModule::class,
        UseCaseModule::class,
        AnalyticsModule::class
    ]
)
interface AppComponent {

    fun context(): Context

    fun inject(activity: SplashActivity)
    fun inject(activity: AuthActivity)
    fun inject(activity: MainActivity)


    fun inject(fragment: AccountFragment)
    fun inject(fragment: ReportsFragment)
}