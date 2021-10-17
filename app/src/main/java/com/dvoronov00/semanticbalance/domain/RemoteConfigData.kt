package com.dvoronov00.semanticbalance.domain

data class RemoteConfigData(
    val isBalanceReplenishmentEnabled: Boolean = false,
    val supportWorkTime: String = "",
    val supportTelephone: String = "",
)