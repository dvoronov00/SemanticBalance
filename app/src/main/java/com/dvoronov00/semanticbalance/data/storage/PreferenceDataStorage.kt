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
        context.getSharedPreferences(USER, Context.MODE_PRIVATE)

    private val accountSP: SharedPreferences =
        context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE)


    override fun getUser(): Observable<User> {
        return Observable.create {
            val username = userSP.getString(USER_USERNAME, null)
            val password = userSP.getString(USER_PASSWORD, null)
            val session = userSP.getString(USER_SESSION_ID, null)
            val expireTime = userSP.getLong(USER_SESSION_EXPIRED_TIME, 0)

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
            .putString(USER_USERNAME, user.username)
            .putString(USER_PASSWORD, user.password)
            .putString(USER_SESSION_ID, user.sessionId)
            .putLong(USER_SESSION_EXPIRED_TIME, user.sessionExpiredTime)
            .apply()
    }

    override fun getAccount(): Observable<Account> {
        return Observable.create {
            val id = accountSP.getInt(ACCOUNT_ID, -1)
            val balance = accountSP.getString(ACCOUNT_BALANCE, null)
            val tariffName = accountSP.getString(ACCOUNT_TARIFF_NAME, null)
            val subscriptionFee = accountSP.getString(ACCOUNT_SUBSCRIPTION_FEE, null)
            val state = accountSP.getString(ACCOUNT_STATE, null)
            val stringServices = accountSP.getString(ACCOUNT_SERVICES, null)

            if (id >= 0 && !balance.isNullOrEmpty()) {
                it.onNext(
                    Account(
                        id = id,
                        balance = balance,
                        tariffName = tariffName,
                        subscriptionFee = subscriptionFee,
                        state = state,
                        services = Gson()
                            .fromJson(stringServices, Array<Service>::class.java)
                            .toList()
                    )
                )
            } else it.onError(AccountParseErrorException())
        }
    }

    override fun saveAccount(account: Account) {
        accountSP
            .edit()
            .putInt(ACCOUNT_ID, account.id)
            .putString(ACCOUNT_BALANCE, account.balance)
            .putString(ACCOUNT_TARIFF_NAME, account.tariffName)
            .putString(ACCOUNT_SUBSCRIPTION_FEE, account.subscriptionFee)
            .putString(ACCOUNT_STATE, account.state)
            .putString(ACCOUNT_SERVICES, Gson().toJson(account.services))
            .apply()
    }

    override fun clearData() {
        userSP
            .edit()
            .clear()
            .apply()

        accountSP
            .edit()
            .clear()
            .apply()
    }

    companion object {
        private const val USER = "user"
        private const val USER_USERNAME = "username"
        private const val USER_PASSWORD = "password"
        private const val USER_SESSION_ID = "sessionId"
        private const val USER_SESSION_EXPIRED_TIME = "sessionExpiredTime"

        private const val ACCOUNT = "account"
        private const val ACCOUNT_ID = "id"
        private const val ACCOUNT_BALANCE = "balance"
        private const val ACCOUNT_TARIFF_NAME = "tariffName"
        private const val ACCOUNT_SUBSCRIPTION_FEE = "subscriptionFee"
        private const val ACCOUNT_STATE = "state"
        private const val ACCOUNT_SERVICES = "services"
    }

}