package com.dvoronov00.semanticbalance.presentation.di.module

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics() = Firebase.analytics
}