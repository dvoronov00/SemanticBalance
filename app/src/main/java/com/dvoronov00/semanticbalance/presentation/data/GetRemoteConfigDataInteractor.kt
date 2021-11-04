package com.dvoronov00.semanticbalance.presentation.data

import com.dvoronov00.semanticbalance.domain.RemoteConfigData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

class GetRemoteConfigDataInteractor @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {

    fun execute(): RemoteConfigData = RemoteConfigData(
        isBalanceReplenishmentEnabled = remoteConfig.getBoolean(IS_BALANCE_REPLENISHMENT_ENABLED_KEY),
        supportWorkTime = remoteConfig.getString(SUPPORT_WORK_TIME_KEY),
        supportTelephone = remoteConfig.getString(SUPPORT_WORK_TELEPHONE_KEY),
        payment = RemoteConfigData.Payment(
            methodHintText = remoteConfig.getString(PAYMENT_METHOD_HINT_KEY),
            sberDeeplink = remoteConfig.getString(PAYMENT_SBER_DEEPLINK_KEY),
            qiwiDeeplink = remoteConfig.getString(PAYMENT_QIWI_DEEPLINK_KEY),
            bankcardDeeplink = remoteConfig.getString(PAYMENT_BANKCARD_DEEPLINK_KEY),
        )
    )

    companion object {
        private const val IS_BALANCE_REPLENISHMENT_ENABLED_KEY = "is_balance_replenishment_enabled"
        private const val SUPPORT_WORK_TIME_KEY = "semantic_support_worktime"
        private const val SUPPORT_WORK_TELEPHONE_KEY = "semantic_support_telephone"

        private const val PAYMENT_METHOD_HINT_KEY = "payment_method_hint"
        private const val PAYMENT_SBER_DEEPLINK_KEY = "sber_semantic_deeplink"
        private const val PAYMENT_QIWI_DEEPLINK_KEY = "qiwi_semantic_url"
        private const val PAYMENT_BANKCARD_DEEPLINK_KEY = "bankcard_semantic_url"
    }
}