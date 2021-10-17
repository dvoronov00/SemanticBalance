package com.dvoronov00.semanticbalance.domain.usecase.analytic

import com.dvoronov00.semanticbalance.data.extension.calculateExistingDays
import com.dvoronov00.semanticbalance.data.extension.toFormattedDate
import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.dvoronov00.semanticbalance.domain.model.Account
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.profile.Attribute
import com.yandex.metrica.profile.UserProfile
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TrackUserProfileInteractor @Inject constructor() {
    fun track(account: Account) {
        Timber.d("Track user profile: $account")
        val calculatedExistingDays = account.calculateExistingDays()
        val balanceExpiredTime = getBalanceExpiredTimestamp(calculatedExistingDays)

        val userProfile = UserProfile.newBuilder()
            .apply {
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.BALANCE)
                        .withValue(account.balance?.toDoubleOrNull() ?: 0.0)
                )
                apply(
                    Attribute.customString(AnalyticKey.UserProfile.TARIFF)
                        .withValue(account.tariffName ?: UNDEFINED_VAL)
                )
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.SUBSCRIPTION_FEE)
                        .withValue(account.subscriptionFee?.toDoubleOrNull() ?: 0.0)
                )
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.EXISTING_DAYS)
                        .withValue(calculatedExistingDays.toDouble())
                )
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.BALANCE_WILL_EXPIRED)
                        .withValue(balanceExpiredTime.toDouble())
                )
                apply(
                    Attribute.customString(AnalyticKey.UserProfile.UPDATE_DATE)
                        .withValue(Calendar.getInstance().time.toFormattedDate())
                )
            }
            .build()

        YandexMetrica.setUserProfileID(account.id.toString())
        YandexMetrica.reportUserProfile(userProfile)

        OneSignal.sendTag("user_id", account.id.toString().trim())
        OneSignal.sendTag("user_balance", account.balance?.trim())
        OneSignal.sendTag("user_existing_days", calculatedExistingDays.toString().trim())
        OneSignal.sendTag("user_tariff", account.tariffName?.trim())
        OneSignal.sendTag("user_balance_will_expired", balanceExpiredTime.toString())
    }

    private fun getBalanceExpiredTimestamp(existingDays: Int): Long = Calendar
        .getInstance()
        .apply {
            set(Calendar.HOUR, 12)
            set(Calendar.MINUTE, 10)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_MONTH, existingDays)
        }.time
        .time / 1000

    companion object {
        private const val UNDEFINED_VAL = "Неизвестное значение"
    }
}
