package com.dvoronov00.semanticbalance.domain.usecase

import com.yandex.metrica.YandexMetrica
import timber.log.Timber
import javax.inject.Inject

class TrackAnalyticInteractor @Inject constructor(
){
    fun track(eventName: String, data: Map<String, Any>? = null) {
        Timber.d("Track analytic: $eventName; data: $data")
        YandexMetrica.reportEvent(eventName, data)
    }
}
