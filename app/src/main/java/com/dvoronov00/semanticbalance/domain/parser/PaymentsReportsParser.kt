package com.dvoronov00.semanticbalance.domain.parser

import com.dvoronov00.semanticbalance.domain.model.PaymentReport

interface PaymentsReportsParser {
    fun getPaymentsReports(): List<PaymentReport>
}