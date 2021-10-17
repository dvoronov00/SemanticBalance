package com.dvoronov00.semanticbalance.domain.usecase.analytic

import com.dvoronov00.semanticbalance.data.calculateExistingDays
import com.dvoronov00.semanticbalance.data.toFormattedDate
import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.dvoronov00.semanticbalance.domain.Const
import com.dvoronov00.semanticbalance.domain.model.Account
import com.onesignal.OneSignal
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.profile.Attribute
import com.yandex.metrica.profile.UserProfile
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class TrackUserProfileInteractor @Inject constructor() {
    fun track(account: Account) {
        Timber.d("Track user profile: $account")
        val calculateExistingDays = account.calculateExistingDays()
        // TODO: Переделать логику выссчитывания balanceExpiredTime
        val balanceExpiredTime = Calendar
            .getInstance()
            .apply {
                add(Calendar.DAY_OF_MONTH, calculateExistingDays)
            }.time
            .time / 1000

        val userProfile = UserProfile.newBuilder()
            .apply {
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.BALANCE)
                        .withValue(account.balance?.toDoubleOrNull() ?: 0.0)
                )
                apply(
                    Attribute.customString(AnalyticKey.UserProfile.TARIFF)
                        .withValue(account.tariffName ?: Const.UNDEFINED_VAL)
                )
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.SUBSCRIPTION_FEE)
                        .withValue(account.subscriptionFee?.toDoubleOrNull() ?: 0.0)
                )
                apply(
                    Attribute.customNumber(AnalyticKey.UserProfile.EXISTING_DAYS)
                        .withValue(calculateExistingDays.toDouble())
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
        OneSignal.sendTag("user_existing_days", calculateExistingDays.toString().trim())
        OneSignal.sendTag("user_tariff", account.tariffName?.trim())
        OneSignal.sendTag("user_balance_will_expired", balanceExpiredTime.toString())
    }
}
