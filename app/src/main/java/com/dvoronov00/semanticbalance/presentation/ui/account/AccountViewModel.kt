package com.dvoronov00.semanticbalance.presentation.ui.account

import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.AnalyticKey
import com.dvoronov00.semanticbalance.domain.RemoteConfigData
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.domain.repository.NewsRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.usecase.GetAccountDataUseCase
import com.dvoronov00.semanticbalance.domain.usecase.analytic.TrackScreenHasErrorsInteractor
import com.dvoronov00.semanticbalance.domain.usecase.analytic.TrackScreenShownInteractor
import com.dvoronov00.semanticbalance.domain.usecase.analytic.TrackUserProfileInteractor
import com.dvoronov00.semanticbalance.presentation.data.GetRemoteConfigDataInteractor
import com.dvoronov00.semanticbalance.presentation.data.mappers.AccountDataMapper
import com.dvoronov00.semanticbalance.presentation.ui.account.model.AccountData
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
    private val router: Router,
    private val trackScreenShownInteractor: TrackScreenShownInteractor,
    private val trackScreenHasErrorsInteractor: TrackScreenHasErrorsInteractor,
    private val trackUserProfileInteractor: TrackUserProfileInteractor,
    private val accountDataMapper: AccountDataMapper,
    private val getRemoteConfigDataInteractor: GetRemoteConfigDataInteractor,
) : ViewModel() {
    val remoteConfigDataRelay: BehaviorRelay<RemoteConfigData> = BehaviorRelay.create()
    val accountDataRelay: BehaviorRelay<DataState<AccountData>> = BehaviorRelay.create()
    val newsDataRelay: BehaviorRelay<DataState<List<News>>> = BehaviorRelay.create()
    val logoutRelay: PublishRelay<Boolean> = PublishRelay.create()

    private val disposeBag = arrayListOf<Disposable>()

    init {
        loadScreenData()
    }

    fun onFragmentViewCreated() {
        remoteConfigDataRelay.accept(getRemoteConfigDataInteractor.execute())
    }

    fun onFragmentStart() {
        trackScreenShownInteractor.track(AnalyticKey.Account.SCREEN_NAME)
    }

    fun loadScreenData() {
        getAccountData()
        getNews()
    }

    private fun getAccountData() {
        val disposable = getAccountDataUseCase.getAccountData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { accountDataRelay.accept(DataState.Loading()) }
            .subscribe({ account ->
                storageRepository.saveAccount(account)
                accountDataRelay.accept(DataState.Success(accountDataMapper.map(account)))
                trackUserProfileInteractor.track(account)
            }, { throwable ->
                accountDataRelay.accept(DataState.Failure(throwable))
                trackScreenHasErrorsInteractor.track(
                    screenName = AnalyticKey.Account.SCREEN_NAME,
                    error = throwable
                )
            })

        disposeBag.add(disposable)
    }

    private fun getNews() {
        val disposable = newsRepository.getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { newsDataRelay.accept(DataState.Loading()) }
            .subscribe({ newsList ->
                newsDataRelay.accept(DataState.Success(newsList.take(2)))
            }, { throwable ->
                newsDataRelay.accept(DataState.Failure(throwable))
                trackScreenHasErrorsInteractor.track(
                    screenName = AnalyticKey.Account.NEWS_NAME,
                    error = throwable
                )
            })

        disposeBag.add(disposable)
    }

    private fun logout() {
        storageRepository.clearData()
        logoutRelay.accept(true)
    }

    fun onMenuItemLogoutClick() {
        logout()
    }

    fun onMenuItemReportsClick() {
        router.navigateTo(ReportsFragment.screen())
    }

    fun onPayCardViewClick() {
        router.navigateTo(PaymentMethodFragment.screen())
    }

    private fun cancelAll() {
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
    }

    override fun onCleared() {
        cancelAll()
        super.onCleared()
    }
}
