package com.dvoronov00.semanticbalance.domain

object AnalyticKey {

    object Common {
        const val SCREEN = "Экраны"
        const val ERRORS = "Ошибки"
    }

    object Account {
        const val SCREEN_NAME = "Аккаунт"
        const val NEWS_NAME = "Новости"

    }

    object UserProfile {
        const val BALANCE = "Баланс"
        const val TARIFF = "Тариф"
        const val EXISTING_DAYS = "Осталось дней"
        const val BALANCE_WILL_EXPIRED = "Balance expired time"
        const val SUBSCRIPTION_FEE = "Абонентская плата"
        const val UPDATE_DATE = "Дата обновления"
    }

    /*
                    analytics.setUserId(account.id.toString().trim())
                analytics.setUserProperty("user_balance", account.balance?.trim())
                analytics.setUserProperty("user_existing_days", calculatedDays.toString().trim())
                analytics.setUserProperty("user_tariff", account.tariffName?.trim())


     */

    object Reports {
        const val SCREEN_NAME = "Отчёты"
    }
}
