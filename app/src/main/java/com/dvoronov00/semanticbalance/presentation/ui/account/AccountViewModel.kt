package com.dvoronov00.semanticbalance.presentation.ui.account

import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.domain.repository.NewsRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.usecase.GetAccountDataUseCase
import com.dvoronov00.semanticbalance.presentation.ui.paymentMethod.PaymentMethodFragment
import com.dvoronov00.semanticbalance.presentation.ui.reports.ReportsFragment
import com.github.terrakok.cicerone.Router
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val storageRepository: StorageRepository,
    private val getAccountDataUseCase: GetAccountDataUseCase,
    private val router: Router
) : ViewModel() {
    private val TAG = "AccountViewModel"

    val accountDataRelay: BehaviorRelay<DataState<Account>> = BehaviorRelay.create()
    val newsDataRelay: BehaviorRelay<DataState<List<News>>> = BehaviorRelay.create()
    val logoutRelay: PublishRelay<Boolean> = PublishRelay.create()

    private val disposeBag = arrayListOf<Disposable>()

    init {
        getAccountData()
        getNews()
    }

    fun getAccountData() {
        val disposable = getAccountDataUseCase.getAccountData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { accountDataRelay.accept(DataState.Loading()) }
            .subscribe({ account ->
                storageRepository.saveAccount(account)
                accountDataRelay.accept(DataState.Success(account))
            }, { throwable ->
                accountDataRelay.accept(DataState.Failure(throwable))
            })

        disposeBag.add(disposable)
    }

    fun getNews() {
        val disposable = newsRepository.getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { newsDataRelay.accept(DataState.Loading()) }
            .subscribe({ newsList ->
                newsDataRelay.accept(DataState.Success(newsList.take(2)))
            }, { throwable ->
                newsDataRelay.accept(DataState.Failure(throwable))
            })

        disposeBag.add(disposable)
    }

    fun logout() {
        storageRepository.clearData()
        logoutRelay.accept(true)
    }


    fun navigateToReportsFragment() {
        router.navigateTo(ReportsFragment.screen())
    }

    fun navigateToPaymentMethodFragment() {
        router.navigateTo(PaymentMethodFragment.screen())
    }


    fun cancelAll() {
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
    }

    override fun onCleared() {
        cancelAll()
        super.onCleared()
    }
}