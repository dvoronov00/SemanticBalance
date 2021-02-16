package com.dvoronov00.semanticbalance.presentation.di.module

import com.dvoronov00.semanticbalance.data.remote.JsoupSemanticConnection
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideSemanticConnection() : SemanticConnection {
        return JsoupSemanticConnection()
    }
}