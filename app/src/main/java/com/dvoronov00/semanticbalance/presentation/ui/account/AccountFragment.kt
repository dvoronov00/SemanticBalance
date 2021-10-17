package com.dvoronov00.semanticbalance.presentation.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.data.extension.toTelUri
import com.dvoronov00.semanticbalance.databinding.FragmentAccountBinding
import com.dvoronov00.semanticbalance.domain.RemoteConfigData
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.ui.account.model.AccountData
import com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter.NewsRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter.ServicesRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.auth.AuthActivity
import com.dvoronov00.semanticbalance.presentation.ui.toScreen
import com.github.terrakok.cicerone.Screen
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import javax.inject.Inject


class AccountFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var binding: FragmentAccountBinding? = null

    private val vm: AccountViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AccountViewModel::class.java)
    }

    private val servicesAdapter = ServicesRecyclerAdapter()
    private val newsAdapter = NewsRecyclerAdapter()

    override fun onAttach(context: Context) {
        App.getComponent().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentAccountBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        (activity as AppCompatActivity).setSupportActionBar(fragmentBinding.toolbar)
        setHasOptionsMenu(true)
        return fragmentBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        MenuInflater(requireContext()).inflate(R.menu.account_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuReports -> vm.onMenuItemReportsClick()
            R.id.menuLogout -> vm.onMenuItemLogoutClick()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.onFragmentViewCreated()
        initUI()
        initUX()
        bindViewModel()
    }

    private fun initUI() {
        binding?.recyclerViewServices?.adapter = servicesAdapter
        binding?.recyclerViewNews?.adapter = newsAdapter
    }

    private fun initUX() {
        binding?.swipeRefreshLayout?.setOnRefreshListener(vm::loadScreenData)
        binding?.cardViewPay?.setOnClickListener { vm.onPayCardViewClick() }
    }

    private fun bindViewModel() {
        vm.accountDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumerGetAccountData::accept)

        vm.newsDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumerGetNews::accept)

        vm.logoutRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { finishAndStartLoginActivity() }

        vm.remoteConfigDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumerRemoteConfig::accept)
    }

    override fun onStart() {
        super.onStart()
        vm.onFragmentStart()
    }

    private val consumerGetNews = Consumer<DataState<List<News>>> { result ->
        when (result) {
            is DataState.Loading -> {
                newsAdapter.clearList()
                showNewsShimmer()
                binding?.newsError?.root?.visibility = View.GONE
            }
            is DataState.Success -> {
                binding?.newsError?.root?.visibility = View.GONE
                hideNewsShimmer()
                newsAdapter.setList(result.data)
            }
            is DataState.Failure -> {
                hideNewsShimmer()
                binding?.newsError?.root?.visibility = View.VISIBLE
            }
        }
    }

    private val consumerRemoteConfig = Consumer<RemoteConfigData> { config ->
        binding?.let { binding ->
            with(binding) {
                cardViewPay.isVisible = config.isBalanceReplenishmentEnabled
                supportWorktime.text = config.supportWorkTime
                cardViewCallToSupport.setOnClickListener {
                    Intent(
                        Intent.ACTION_DIAL,
                        config.supportTelephone.toTelUri()
                    ).let(::startActivity)
                }
            }
        }
    }

    private val consumerGetAccountData = Consumer<DataState<AccountData>> { result ->
        when (result) {
            is DataState.Loading -> {
                hideAccountViewData()
                showAccountShimmer()
            }
            is DataState.Success -> {
                showAccountData(result.data)
                hideAccountShimmer()
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
            is DataState.Failure -> {
                showErrorConnectionToast()
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    private fun showErrorConnectionToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error_no_connection),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAccountData(data: AccountData) {
        binding?.let {
            with(it) {
                textViewUserAccountId.visibility = View.VISIBLE
                textViewBalance.visibility = View.VISIBLE
                textViewTariffName.visibility = View.VISIBLE
                textViewSubscriptionFee.visibility = View.VISIBLE
                textViewAccountState.visibility = View.VISIBLE

                textViewUserAccountId.text = data.id
                textViewBalance.text = data.balance
                textViewTariffName.text = data.tariffName
                textViewSubscriptionFee.text = data.subscriptionFee
                textViewAccountState.text = data.state
                servicesAdapter.setList(data.serviceList)

                when (data.daysExisting) {
                    is AccountData.DaysExisting.SomeDaysExisting -> {
                        textViewNextPayTitle.visibility = View.VISIBLE
                        textViewExistingDays.visibility = View.VISIBLE
                        textViewHintLowBalance.visibility = View.GONE
                        textViewExistingDays.text = data.daysExisting.daysQuantityText
                    }
                    is AccountData.DaysExisting.ZeroDaysExisting -> {
                        textViewNextPayTitle.visibility = View.GONE
                        textViewExistingDays.visibility = View.GONE
                        textViewHintLowBalance.visibility = View.VISIBLE
                        textViewHintLowBalance.text = data.daysExisting.hintText
                    }
                }
            }
        }
    }

    private fun finishAndStartLoginActivity() {
        val intent = Intent(activity, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        activity?.finish()
    }

    private fun hideAccountViewData() {
        binding?.let {
            with(it) {
                textViewUserAccountId.visibility = View.INVISIBLE
                textViewBalance.visibility = View.INVISIBLE
                textViewTariffName.visibility = View.INVISIBLE
                textViewSubscriptionFee.visibility = View.INVISIBLE
                textViewAccountState.visibility = View.INVISIBLE
                servicesAdapter.clearList()
            }
        }
    }

    private fun showAccountShimmer() {
        binding?.shimmerCardAccount?.visibility = View.VISIBLE
        binding?.shimmerCardInformation?.visibility = View.VISIBLE
        binding?.shimmerCardServices?.visibility = View.VISIBLE
    }

    private fun hideAccountShimmer() {
        binding?.shimmerCardAccount?.visibility = View.GONE
        binding?.shimmerCardInformation?.visibility = View.GONE
        binding?.shimmerCardServices?.visibility = View.GONE
    }

    private fun showNewsShimmer() {
        binding?.shimmerCardNews?.visibility = View.VISIBLE
    }

    private fun hideNewsShimmer() {
        binding?.shimmerCardNews?.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {

        fun screen(): Screen {
            return AccountFragment().toScreen()
        }
    }
}
