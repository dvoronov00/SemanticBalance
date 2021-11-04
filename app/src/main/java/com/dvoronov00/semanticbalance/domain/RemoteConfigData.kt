package com.dvoronov00.semanticbalance.domain

data class RemoteConfigData(
    val isBalanceReplenishmentEnabled: Boolean = false,
    val supportWorkTime: String = "",
    val supportTelephone: String = "",
    val payment: Payment = Payment(),
) {

    data class Payment(
        val methodHintText: String = "",
        val sberDeeplink: String = "",
        val qiwiDeeplink: String = "",
        val bankcardDeeplink: String = "",
    )
}