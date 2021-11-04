package com.dvoronov00.semanticbalance.presentation.ui.account.model

import com.dvoronov00.semanticbalance.domain.model.Service

data class AccountData(
    val id: String,
    val balance: String,
    val tariffName: String,
    val subscriptionFee: String,
    val state: String,
    val serviceList: List<Service>,
    val daysExisting: DaysExisting,
) {

    sealed class DaysExisting {
        data class ZeroDaysExisting(val hintText: String) : DaysExisting()
        data class SomeDaysExisting(val daysQuantityText: String) : DaysExisting()
    }
}