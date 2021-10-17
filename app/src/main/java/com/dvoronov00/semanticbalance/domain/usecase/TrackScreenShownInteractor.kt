package com.dvoronov00.semanticbalance.domain.usecase

import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.yandex.metrica.YandexMetrica
import timber.log.Timber
import javax.inject.Inject

class TrackScreenShownInteractor @Inject constructor(
){
    fun track(screenName: String) {
        Timber.d("Track screen shown: $screenName")
        YandexMetrica.reportEvent(AnalyticKey.Common.SCREEN, mapOf(
            screenName to ""
        ))
    }
}
