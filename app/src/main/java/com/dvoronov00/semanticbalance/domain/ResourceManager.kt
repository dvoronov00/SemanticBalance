package com.dvoronov00.semanticbalance.domain

interface ResourceManager {

    fun getString(resId: Int, vararg params: Any?): String

    fun getQuantityString(pluralsId: Int, quantity: Int): String
}