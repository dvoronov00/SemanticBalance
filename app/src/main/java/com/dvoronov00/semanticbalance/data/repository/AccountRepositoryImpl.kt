package com.dvoronov00.semanticbalance.data.repository

import com.dvoronov00.semanticbalance.data.parser.AccountParserImpl
import com.dvoronov00.semanticbalance.data.parser.PaymentsReportsParserImpl
import com.dvoronov00.semanticbalance.data.parser.ServicesReportsParserImpl
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.PaymentReport
import com.dvoronov00.semanticbalance.domain.model.ServiceReport
import com.dvoronov00.semanticbalance.domain.model.User
import com.dvoronov00.semanticbalance.domain.parser.AccountParser
import com.dvoronov00.semanticbalance.domain.parser.PaymentsReportsParser
import com.dvoronov00.semanticbalance.domain.parser.ServicesReportsParser
import com.dvoronov00.semanticbalance.domain.remote.SemanticConnection
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val semanticConnection: SemanticConnection
) : AccountRepository {

    override fun getAccountData(user: User): Observable<Account> {
        return semanticConnection.getAccountHtml(user)
            .map {
                val parser : AccountParser = AccountParserImpl(it)
                Account(
                    parser.getId(),
                    parser.getBalance(),
                    parser.getTariffName(),
                    parser.getSubscriptionFee(),
                    parser.getAccountState(),
                    parser.getServices()
                )
            }
    }

    override fun getPaymentsReportsData(user: User): Observable<List<PaymentReport>> {
        return semanticConnection.getPaymentsReports(user)
            .map {
                val parser : PaymentsReportsParser = PaymentsReportsParserImpl(it)
                parser.getPaymentsReports()
            }
    }

    override fun getServicesReportsData(user: User): Observable<List<ServiceReport>> {
        return semanticConnection.getServicesReports(user)
            .map {
                val parser : ServicesReportsParser = ServicesReportsParserImpl(it)
                parser.getServicesReports()
            }
    }


}