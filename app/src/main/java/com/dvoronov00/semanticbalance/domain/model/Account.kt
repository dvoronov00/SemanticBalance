package com.dvoronov00.semanticbalance.domain.model

import java.util.*

data class Account(
    val id: Int = -1,
    val balance: String? = null,
    val tariffName: String? = null,
    val subscriptionFee: String? = null,
    val state: String? = null,
    val services: List<Service> = arrayListOf()
) {

    fun calculateExistingDays(): Int {
        var balance: Float = this.balance?.toFloatOrNull() ?: 0.0f
        val subscriptionFee: Float = this.services.sumBy { it.price.toInt() }.toFloat()

        val calendar = Calendar.getInstance()

        // Оплата происходит в 00:10 каждого дня. Если время больше, чем 00:10, значит за сегодня пользователь уже заплатил и считать не нужно
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 1 ||
            (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) >= 10)
        ) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        var daysExist = 0
        while (balance > 0) {
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val payPerDayInThisMonth = subscriptionFee / daysInMonth
            if (balance >= payPerDayInThisMonth) {
                balance -= payPerDayInThisMonth
                daysExist++
            } else break
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return daysExist
    }
}