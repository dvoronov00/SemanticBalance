package com.dvoronov00.semanticbalance.data.repository

import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val semanticConnection: SemanticConnection
)  : AuthRepository {
    override fun auth(username: String, password: String): Observable<User> {
        return semanticConnection.auth(username, password).map { session ->
            val user = User(username, password)
            user.updateSession(session)
            user
        }

    }
}