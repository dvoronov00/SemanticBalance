package com.dvoronov00.semanticbalance.domain.model

data class Account(
    val id: Int = -1,
    val balance: String? = null,
    val tariffName: String? = null,
    val subscriptionFee: String? = null,
    val state: String? = null,
    val services: List<Service> = arrayListOf()
) {

}