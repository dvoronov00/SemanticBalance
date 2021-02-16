package com.dvoronov00.semanticbalance.presentation.di.module

import com.dvoronov00.semanticbalance.data.repository.AccountRepositoryImpl
import com.dvoronov00.semanticbalance.data.repository.AuthRepositoryImpl
import com.dvoronov00.semanticbalance.data.repository.NewsRepositoryImpl
import com.dvoronov00.semanticbalance.data.repository.StorageRepositoryImpl
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import com.dvoronov00.semanticbalance.domain.repository.NewsRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.storage.DataStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module(includes = [
    NetworkModule::class
])
class RepositoryModule {

    @Singleton
    @Provides
    fun provideAccountRepository(semanticConnection: SemanticConnection) : AccountRepository {
        return AccountRepositoryImpl(semanticConnection)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(semanticConnection: SemanticConnection) : AuthRepository {
        return AuthRepositoryImpl(semanticConnection)
    }

    @Singleton
    @Provides
    fun provideNewsRepository(semanticConnection: SemanticConnection) : NewsRepository {
        return NewsRepositoryImpl(semanticConnection)
    }



    @Singleton
    @Provides
    fun provideDataStorageRepository(dataStorage: DataStorage) : StorageRepository {
        return StorageRepositoryImpl(dataStorage)
    }




}