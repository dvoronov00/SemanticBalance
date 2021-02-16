package com.dvoronov00.semanticbalance.domain.repository

import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.PaymentReport
import com.dvoronov00.semanticbalance.domain.model.ServiceReport
import com.dvoronov00.semanticbalance.domain.model.User
import io.reactivex.rxjava3.core.Observable

interface AccountRepository {

    fun getAccountData(user: User) : Observable<Account>
    fun getPaymentsReportsData(user: User) : Observable<List<PaymentReport>>
    fun getServicesReportsData(user: User) : Observable<List<ServiceReport>>

}