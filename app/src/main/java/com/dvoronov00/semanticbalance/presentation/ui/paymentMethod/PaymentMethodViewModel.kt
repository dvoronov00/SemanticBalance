package com.dvoronov00.semanticbalance.presentation.ui.paymentMethod

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.Report
import com.dvoronov00.semanticbalance.domain.repository.AccountRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.domain.usecase.AuthUserUseCase
import com.github.terrakok.cicerone.Router
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
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