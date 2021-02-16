package com.dvoronov00.semanticbalance.domain.remote

import com.dvoronov00.semanticbalance.domain.model.User
import io.reactivex.rxjava3.core.Observable

interface SemanticConnection {
    fun auth(username: String, password: String) : Observable<String>
    fun getAccountHtml(user: User) : Observable<String>
    fun getPaymentsReports(user: User) : Observable<String>
    fun getServicesReports(user: User) : Observable<String>
    fun getNewsHtml() : Observable<String>
}