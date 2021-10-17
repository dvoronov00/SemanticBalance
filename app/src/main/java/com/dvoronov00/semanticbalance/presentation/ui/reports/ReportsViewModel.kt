package com.dvoronov00.semanticbalance.presentation.ui.reports

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.dvoronov00.semanticbalance.domain.Const.UNDEFINED_ERROR
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.Report
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import com.dvoronov00.semanticbalance.domain.usecase.AuthUserUseCase
import com.dvoronov00.semanticbalance.domain.usecase.TrackAnalyticInteractor
import com.dvoronov00.semanticbalance.domain.usecase.TrackScreenHasErrorsInteractor
import com.dvoronov00.semanticbalance.domain.usecase.TrackScreenShownInteractor
import com.github.terrakok.cicerone.Router
import com.google.firebase.analytics.FirebaseAnalytics
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import javax.inject.Inject

class ReportsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authUserUseCase: AuthUserUseCase,
    private val router: Router,
    private val trackScreenShownInteractor: TrackScreenShownInteractor,
    private val trackScreenHasErrorsInteractor: TrackScreenHasErrorsInteractor,
) : ViewModel() {

    val reportsDataRelay: PublishRelay<DataState<List<Report>>> = PublishRelay.create()

    private val disposeBag = arrayListOf<Disposable>()

    fun onFragmentStart() {
        trackScreenShownInteractor.track(AnalyticKey.Reports.SCREEN_NAME)
    }

    fun getServicesReports() {
        val disposable = authUserUseCase.getAuthorizedUser()
            .switchMap { accountRepository.getServicesReportsData(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSubscribe { reportsDataRelay.accept(DataState.Loading()) }
            .subscribe({ serviceReportList ->
                reportsDataRelay.accept(DataState.Success(serviceReportList))
            }, { throwable ->
                reportsDataRelay.accept(DataState.Failure(throwable))
            })

        disposeBag.add(disposable)
    }

    fun getPaymentsReports() {

        val disposable = authUserUseCase.getAuthorizedUser()
            .switchMap { accountRepository.getPaymentsReportsData(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doOnSubscribe { reportsDataRelay.accept(DataState.Loading()) }
            .subscribe({ paymentReportList ->
                val result = paymentReportList.sortedByDescending {
                    SimpleDateFormat("dd.MM.yyyy hh:mm").parse(it.date)
                }
                reportsDataRelay.accept(DataState.Success(result))
            }, { throwable ->
                reportsDataRelay.accept(DataState.Failure(throwable))
                trackScreenHasErrorsInteractor.track(
                    screenName = AnalyticKey.Reports.SCREEN_NAME,
                    data = mapOf(
                        (throwable.message ?: UNDEFINED_ERROR) to ""
                    )
                )
            })

        disposeBag.add(disposable)
    }

    fun cancelAll() {
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
    }

    override fun onCleared() {
        cancelAll()
        super.onCleared()
    }

    fun back() {
        router.exit()
    }

}
