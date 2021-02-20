package com.dvoronov00.semanticbalance.presentation.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.di.scope.ViewModelKey
import com.dvoronov00.semanticbalance.presentation.ui.account.AccountViewModel
import com.dvoronov00.semanticbalance.presentation.ui.paymentMethod.PaymentMethodViewModel
import com.dvoronov00.semanticbalance.presentation.ui.reports.ReportsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [
    RepositoryModule::class
])
abstract class ViewModelModule {

    @Binds
    internal abstract fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun accountViewModel(viewModel: AccountViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReportsViewModel::class)
    internal abstract fun reportsViewModel(viewModel: ReportsViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentMethodViewModel::class)
    internal abstract fun paymentMethodViewModel(viewModel: PaymentMethodViewModel) : ViewModel



}