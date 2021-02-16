package com.dvoronov00.semanticbalance.domain.storage

import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.User
import io.reactivex.rxjava3.core.Observable

interface DataStorage {

    fun getUser() : Observable<User>
    fun saveUser(user: User)
    fun getAccount() : Observable<Account>
    fun saveAccount(account: Account)

    fun clearData()
}