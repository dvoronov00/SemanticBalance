package com.dvoronov00.semanticbalance.data.exception

class UserSessionExpiredException : Exception() {
    override val message: String?
        get() = "User session expired"
}