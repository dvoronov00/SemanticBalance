package com.dvoronov00.semanticbalance.data.repository

import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.storage.DataStorage
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val dataStorage: DataStorage
)  : StorageRepository {
    override fun getUser(): Observable<User> = dataStorage.getUser()

    override fun saveUser(user: User) {
        dataStorage.saveUser(user)
    }

    override fun getAccount(): Observable<Account> = dataStorage.getAccount()

    override fun saveAccount(account: Account) {
        dataStorage.saveAccount(account)
    }

    override fun clearData() {
        dataStorage.clearData()
    }
}