package com.dvoronov00.semanticbalance.presentation.ui.account

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.data.calculateExistingDays
import com.dvoronov00.semanticbalance.databinding.FragmentAccountBinding
import com.dvoronov00.semanticbalance.domain.model.Account
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.domain.model.News
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter.NewsRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.account.servicesAdapter.ServicesRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.auth.AuthActivity
import com.dvoronov00.semanticbalance.presentation.ui.toScreen
import com.dvoronov00.semanticbalance.presentation.worker.CheckBalanceWorker
import com.github.terrakok.cicerone.Screen
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AccountFragment : Fragment() {
    private val TAG = "AccountFragment"

    companion object {

        fun screen(): Screen {
            return AccountFragment().toScreen()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig


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
    ): View? {
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
            R.id.menuReports -> {
                vm.navigateToReportsFragment()
            }
            R.id.menuLogout -> {
                WorkManager.getInstance(requireContext()).cancelAllWorkByTag("BalanceChecker")
                vm.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            bindRemoteConfig()
        }
        binding?.let { binding ->
            binding.recyclerViewServices.adapter = servicesAdapter
            binding.recyclerViewNews.adapter = newsAdapter

            binding.recyclerViewServices.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.recyclerViewNews.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

            binding.swipeRefreshLayout.setOnRefreshListener {
                vm.getAccountData()
                vm.getNews()
            }
            binding.cardViewPay.setOnClickListener {
                vm.navigateToPaymentMethodFragment()
            }

            bindRemoteConfig()
        }

        vm.accountDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                consumerGetAccountData.accept(it)
            }

        vm.newsDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                consumerGetNews.accept(it)
            }

        vm.logoutRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(activity, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                activity?.finish()
            }
    }

    private fun bindRemoteConfig() {
        binding?.let { binding ->
            binding.cardViewPay.isVisible =
                remoteConfig.getBoolean("is_balance_replenishment_enabled")
            binding.supportWorktime.text = remoteConfig.getString("semantic_support_worktime")
            binding.cardViewCallToSupport.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + remoteConfig.getString("semantic_support_telephone"))
                )
                startActivity(intent)
            }
        }
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

    private val consumerGetAccountData = Consumer<DataState<Account>> { result ->
        when (result) {
            is DataState.Loading -> {
                hideAccountViewData()
                showAccountShimmer()
            }
            is DataState.Success -> {
                showAccountData(account = result.data)
                hideAccountShimmer()
                binding?.swipeRefreshLayout?.isRefreshing = false
                runCheckBalanceWorker(result.data.calculateExistingDays())
            }
            is DataState.Failure -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_no_connection),
                    Toast.LENGTH_SHORT
                ).show()
                binding?.swipeRefreshLayout?.isRefreshing = false
            }
        }
    }

    private fun runCheckBalanceWorker(existingDays: Int) {
        val tag = "BalanceChecker"
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(tag);
        if (existingDays == 1 || existingDays == 2) {
            val workerConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val initDelay = 24 - Calendar.getInstance()
                .get(Calendar.HOUR) + 12 // Устанавливаем делей так, чтобы уведомление пришло в ~12-13 часов
            val checkBalanceWorker = OneTimeWorkRequest.Builder(CheckBalanceWorker::class.java)
                .addTag(tag)
                .setConstraints(workerConstraints)
                .setInitialDelay(initDelay.toLong(), TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(requireContext()).enqueue(checkBalanceWorker)
        }
    }

    private fun showAccountData(account: Account) {
        binding?.let {
            with(it) {
                textViewUserAccountId.text = getString(R.string.accountId, account.id)
                textViewBalance.text = getString(R.string.moneyInRoubles, account.balance)
                textViewTariffName.text = account.tariffName
                textViewSubscriptionFee.text =
                    getString(R.string.moneyInRoublesPerMonth, account.subscriptionFee)
                textViewAccountState.text = account.state
                servicesAdapter.setList(account.services)

                val calculatedDays = account.calculateExistingDays()
                if (calculatedDays > 0) {
                    textViewNextPayTitle.visibility = View.VISIBLE
                    textViewExistingDays.visibility = View.VISIBLE
                    textViewHintLowBalance.visibility = View.GONE
                    textViewExistingDays.text = resources.getQuantityString(
                        R.plurals.afterDays,
                        calculatedDays,
                        calculatedDays
                    )
                } else {
                    textViewNextPayTitle.visibility = View.GONE
                    textViewExistingDays.visibility = View.GONE
                    textViewHintLowBalance.visibility = View.VISIBLE
                    if (account.state.equals("Разблокирован")) {
                        textViewHintLowBalance.text = getString(R.string.hintLowBalance)
                    } else {
                        textViewHintLowBalance.text = getString(R.string.hintNoBalance)
                    }
                }

                textViewUserAccountId.visibility = View.VISIBLE
                textViewBalance.visibility = View.VISIBLE
                textViewTariffName.visibility = View.VISIBLE
                textViewSubscriptionFee.visibility = View.VISIBLE
                textViewAccountState.visibility = View.VISIBLE
            }
        }
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
}