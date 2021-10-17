package com.dvoronov00.semanticbalance.data.extension

import com.dvoronov00.semanticbalance.data.exception.UserIsNotAuthorizedException
import com.dvoronov00.semanticbalance.data.exception.UserSessionExpiredException
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.User
import java.text.SimpleDateFormat
import java.util.*

fun Account.calculateExistingDays(): Int {
    val balance: Float? = this.balance?.toFloat()
    val subscriptionFee: Float = this.services.sumBy { it.price.toInt() }.toFloat()

    val calendar = Calendar.getInstance()

    // Оплата происходит в 00:10 каждого дня. Если время больше, чем 00:10, значит за сегодня пользователь уже заплатил и считать не нужно
    if (calendar.get(Calendar.HOUR_OF_DAY) >= 1
        ||
        (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE ) >= 10)
    ) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    var tmpBalance = balance!!
    var daysExist = 0
    var day = 1
    while (tmpBalance > 0) {
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val payPerDayInThisMonth = subscriptionFee / daysInMonth
        if (tmpBalance >= payPerDayInThisMonth) {
            tmpBalance -= payPerDayInThisMonth
            daysExist++
        } else {
            break
        }
        day++
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return daysExist
}


fun User.checkSessionValid() : Throwable? {
    return when {
        session.isNullOrEmpty() -> {
            UserIsNotAuthorizedException()
        }
        isSessionExpired() -> {
            UserSessionExpiredException()
        }
        else -> {
            null
        }
    }
}

fun Date.toFormattedDate() =
    SimpleDateFormat("dd.MM.yyyy hh:mm", Locale("ru", "RU"))
        .format(this)
