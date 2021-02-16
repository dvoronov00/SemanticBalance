package com.dvoronov00.semanticbalance.domain.model


data class User(
    val username: String,
    val password: String,
    var session: String? = null,
    var expireTime: Long = 0
) {

    private val sessionLifetime = 4 * 60 * 1000 // Время жизни сессии 5 минут, но мы ограничиваем 4

    fun updateSession(session: String){
        this.session = session
        this.expireTime = System.currentTimeMillis() + sessionLifetime
    }

    fun isSessionExpired() : Boolean{
        return System.currentTimeMillis() >= expireTime
    }
}