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
    )

    companion object {
        private const val IS_BALANCE_REPLENISHMENT_ENABLED_KEY = "is_balance_replenishment_enabled"
        private const val SUPPORT_WORK_TIME_KEY = "semantic_support_worktime"
        private const val SUPPORT_WORK_TELEPHONE_KEY = "semantic_support_telephone"
    }
}