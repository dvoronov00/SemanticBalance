package com.dvoronov00.semanticbalance.domain.usecase.analytic

import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.yandex.metrica.YandexMetrica
import timber.log.Timber
import javax.inject.Inject

class TrackScreenHasErrorsInteractor @Inject constructor() {
    fun track(screenName: String, error: Throwable? = null) {
        Timber.d("Track error: $screenName; error: $error")
        YandexMetrica.reportError(
            AnalyticKey.Common.ERRORS,
            error
        )
    }
}
