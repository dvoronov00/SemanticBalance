package com.dvoronov00.semanticbalance.domain.model

sealed class Report
data class ServiceReport(
    val date: String,
    val name: String,
    val price: String
) : Report()

data class PaymentReport(
    val date: String,
    val paymentAmount: String,
    val comment: String? = null,
    val paymentMethod: String? = null
) : Report()
