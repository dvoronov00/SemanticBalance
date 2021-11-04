package com.dvoronov00.semanticbalance.domain.model

import com.dvoronov00.semanticbalance.data.exception.UserIsNotAuthorizedException
import com.dvoronov00.semanticbalance.data.exception.UserSessionExpiredException

data class User(
    val username: String,
    val password: String,
    var sessionId: String? = null,
    var sessionExpiredTime: Long = 0
) {

    fun updateSession(session: String){
        this.sessionId = session
        this.sessionExpiredTime = System.currentTimeMillis() + SESSION_LIFE_TIME
    }

    fun isSessionExpired() : Boolean{
        return System.currentTimeMillis() >= sessionExpiredTime
    }

    fun checkSessionValid(): Throwable? {
        return when {
            sessionId.isNullOrEmpty() -> UserIsNotAuthorizedException()
            isSessionExpired() -> UserSessionExpiredException()
            else -> null
        }
    }

    companion object {
        /** Время жизни сессии 5 минут, но мы ограничиваем 4 */
        private const val SESSION_LIFE_TIME = 4 * 60 * 1000
    }

}