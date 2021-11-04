package com.dvoronov00.semanticbalance.presentation.di.module

import android.content.Context
import com.dvoronov00.semanticbalance.data.ResourceManagerImpl
import com.dvoronov00.semanticbalance.data.storage.PreferenceDataStorage
import com.dvoronov00.semanticbalance.domain.ResourceManager
import com.dvoronov00.semanticbalance.domain.storage.DataStorage
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providesDataStorage(context: Context) : DataStorage = PreferenceDataStorage(context)

    @Provides
    fun providesResourceManager(context: Context) : ResourceManager = ResourceManagerImpl(context)
}