package com.dvoronov00.semanticbalance.presentation.ui.paymentMethod

import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.github.terrakok.cicerone.Router
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PaymentMethodViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val router: Router
) : ViewModel() {
    private val TAG = "ReportsViewModel"


    val accountIdRelay: BehaviorRelay<Int> = BehaviorRelay.create()
    val errorRelay: BehaviorRelay<Throwable> = BehaviorRelay.create()


    private val disposeBag = arrayListOf<Disposable>()

    init {
        getAccountId()
    }

    private fun getAccountId() {
        val disposable = storageRepository.getAccount()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    if (it.id <= 0) {
                        errorRelay.accept(Exception())
                    } else {
                        accountIdRelay.accept(it.id)
                    }
                },
                {
                    errorRelay.accept(it)
                }
            )
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