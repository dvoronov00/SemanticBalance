package com.dvoronov00.semanticbalance.domain.usecase

import android.os.Bundle
import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import timber.log.Timber
import javax.inject.Inject

class TrackScreenHasErrorsInteractor @Inject constructor(
) {
    fun track(screenName: String, data: Map<String, Any>? = null) {
        Timber.d("Track error: $screenName; data: $data")
        YandexMetrica.reportEvent(
            AnalyticKey.Common.ERRORS,
            mapOf(
                screenName to data
            )
        )
    }
}
