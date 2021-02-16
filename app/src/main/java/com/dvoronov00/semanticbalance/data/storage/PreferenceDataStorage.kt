package com.dvoronov00.semanticbalance.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.dvoronov00.semanticbalance.data.exception.AccountParseErrorException
import com.dvoronov00.semanticbalance.data.exception.UserNotFoundException
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.Service
import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.storage.DataStorage
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable

class PreferenceDataStorage(context: Context) : DataStorage {

    private val userSP: SharedPreferences =
        context.getSharedPreferences("user", Context.MODE_PRIVATE)

    private val accountSP: SharedPreferences =
        context.getSharedPreferences("account", Context.MODE_PRIVATE)


    override fun getUser(): Observable<User> {
        return Observable.create {
            val username = userSP.getString("username", null)
            val password = userSP.getString("password", null)
            val session = userSP.getString("session", null)
            val expireTime = userSP.getLong("expireTime", 0)

            if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
                it.onError(UserNotFoundException())
            } else {
                it.onNext(User(username, password, session, expireTime))
            }
        }

    }

    override fun saveUser(user: User) {
        userSP
            .edit()
            .putString("username", user.username)
            .putString("password", user.password)
            .putString("session", user.session)
            .putLong("expireTime", user.expireTime)
            .apply()
    }

    override fun getAccount(): Observable<Account> {
        return Observable.create {
            val id = accountSP.getInt("id", -1)
            val balance = accountSP.getString("balance", null)
            val tariffName = accountSP.getString("tariffName", null)
            val subscriptionFee = accountSP.getString("subscriptionFee", null)
            val state = accountSP.getString("state", null)
            val stringServices = accountSP.getString("services", null)

            if (id == -1 || balance.isNullOrEmpty()) {
                it.onError(AccountParseErrorException())
            } else {
                val account = Account(
                    id,
                    balance,
                    tariffName,
                    subscriptionFee,
                    state,
                    Gson().fromJson(stringServices, Array<Service>::class.java).toList()
                )
                it.onNext(account)
            }
        }
    }

    override fun saveAccount(account: Account) {
        accountSP
            .edit()
            .putInt("id", account.id)
            .putString("balance", account.balance)
            .putString("tariffName", account.tariffName)
            .putString("subscriptionFee", account.subscriptionFee)
            .putString("state", account.state)
            .putString("services", Gson().toJson(account.services))
            .apply()
    }

    override fun clearData() {
        userSP.edit().clear().apply()
        accountSP.edit().clear().apply()
    }


}