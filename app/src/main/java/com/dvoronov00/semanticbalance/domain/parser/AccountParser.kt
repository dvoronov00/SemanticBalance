package com.dvoronov00.semanticbalance.domain.parser

import com.dvoronov00.semanticbalance.domain.model.Service

interface AccountParser {
    fun getId() : Int
    fun getBalance(): String
    fun getTariffName(): String
    fun getSubscriptionFee() : String
    fun getAccountState() : String
    fun getServices() : List<Service>
}