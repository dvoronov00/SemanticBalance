package com.dvoronov00.semanticbalance.domain.repository

import com.dvoronov00.semanticbalance.domain.model.User
import io.reactivex.rxjava3.core.Observable

interface AuthRepository {
    fun auth(username: String, password: String) : Observable<User>
}