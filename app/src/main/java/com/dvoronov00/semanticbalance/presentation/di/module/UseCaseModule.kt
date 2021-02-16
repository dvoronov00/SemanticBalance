package com.dvoronov00.semanticbalance.presentation.di.module

import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.usecase.AuthUserUseCase
import com.dvoronov00.semanticbalance.domain.usecase.GetAccountDataUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {

    @Provides
    fun provideAuthUserUseCase(
        authRepository: AuthRepository,
        storageRepository: StorageRepository
    ): AuthUserUseCase {
        return AuthUserUseCase(authRepository, storageRepository)
    }

    @Provides
    fun provideGetAccountDataUseCase(
        authUserUseCase: AuthUserUseCase,
        accountRepository: AccountRepository
    ): GetAccountDataUseCase {
        return GetAccountDataUseCase(authUserUseCase, accountRepository)
    }

}