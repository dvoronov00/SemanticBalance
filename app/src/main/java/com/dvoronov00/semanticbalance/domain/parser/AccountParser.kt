package com.dvoronov00.semanticbalance.domain.parser

import com.dvoronov00.semanticbalance.domain.model.Account

interface AccountParser {
    fun getAccount(): Account
}