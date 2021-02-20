package com.dvoronov00.semanticbalance.presentation.ui.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.Report
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import com.dvoronov00.semanticbalance.domain.usecase.AuthUserUseCase
import com.github.terrakok.cicerone.Router
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import javax.inject.Inject

class ReportsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authUserUseCase: AuthUserUseCase,
    private val router: Router
) : ViewModel() {
    private val TAG = "ReportsViewModel"

    val reportsDataRelay: PublishRelay<DataState<List<Report>>> = PublishRelay.create()

    private val disposeBag = arrayListOf<Disposable>()


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
                Log.e(TAG, "getServicesReports: $throwable")
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
                Log.e(TAG, "getPaymentsReports: $throwable")
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