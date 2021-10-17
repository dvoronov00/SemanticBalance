package com.dvoronov00.semanticbalance.presentation.ui.paymentMethod

import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.RemoteConfigData
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.presentation.data.GetRemoteConfigDataInteractor
import com.github.terrakok.cicerone.Router
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PaymentMethodViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val router: Router,
    private val getRemoteConfigDataInteractor: GetRemoteConfigDataInteractor,
) : ViewModel() {
    val remoteConfigRelay: BehaviorRelay<RemoteConfigData> = BehaviorRelay.create()
    val accountIdRelay: BehaviorRelay<Int> = BehaviorRelay.create()
    val errorRelay: BehaviorRelay<Throwable> = BehaviorRelay.create()

    private val disposeBag = arrayListOf<Disposable>()

    init {
        getAccountId()
        remoteConfigRelay.accept(getRemoteConfigDataInteractor.execute())
    }

    private fun getAccountId() {
        storageRepository.getAccount()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.id > 0) accountIdRelay.accept(it.id)
                    else errorRelay.accept(Exception())
                },
                {
                    errorRelay.accept(it)
                }
            ).let(disposeBag::add)
    }

    private fun cancelAll() {
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