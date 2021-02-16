package com.dvoronov00.semanticbalance.domain.usecase

import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class GetAccountDataUseCase @Inject constructor(
    private val authUserUseCase: AuthUserUseCase,
    private val accountRepository: AccountRepository
) {

    fun getAccountData() : Observable<Account> {
        return authUserUseCase.getAuthorizedUser()
            .switchMap { accountRepository.getAccountData(it) }
    }
}