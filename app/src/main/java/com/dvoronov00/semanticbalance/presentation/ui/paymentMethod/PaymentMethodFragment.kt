package com.dvoronov00.semanticbalance.presentation.ui.paymentMethod

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.databinding.FragmentPaymentMethodBinding
import com.dvoronov00.semanticbalance.databinding.FragmentReportsBinding
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.ui.reports.reportsAdapter.ReportsRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.toScreen
import com.github.terrakok.cicerone.Screen
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.tiper.MaterialSpinner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PaymentMethodFragment : Fragment() {
    private val TAG = "PaymentMethodFragment"

    companion object {
        fun screen(): Screen {
            return PaymentMethodFragment().toScreen()
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var binding: FragmentPaymentMethodBinding? = null

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var remoteConfig : FirebaseRemoteConfig


    private val vm: PaymentMethodViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PaymentMethodViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        App.getComponent().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        (activity as AppCompatActivity).setSupportActionBar(fragmentBinding.toolbar)
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { binding ->
            binding.toolbar.setNavigationOnClickListener {
                vm.back()
            }
            if(checkIsSberbankOnlineInstalled()){
                binding.sberCardView.isEnabled = true
                binding.sberCardView.alpha = 1.0f
            }else{
                binding.sberCardView.isEnabled = false
                binding.sberCardView.alpha = 0.55f
            }
            with(remoteConfig){
                binding.paymentMethodHint.text = this.getString("payment_method_hint")

                binding.sberCardView.setOnClickListener {
                    analytics.logEvent("user_payment_sber_click", null)
                    val sberDeeplink = this.getString("sber_semantic_deeplink")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(sberDeeplink))
                    startActivity(intent)
                }

                binding.qiwiCardView.setOnClickListener {
                    analytics.logEvent("user_payment_qiwi_click", null)
                    val qiwiUrl = this.getString("qiwi_semantic_url")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(qiwiUrl))
                    startActivity(intent)
                }

                binding.bankCardView.setOnClickListener {
                    analytics.logEvent("user_payment_bankcard_click", null)
                    val bankcardUrl = this.getString("bankcard_semantic_url")
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(bankcardUrl))
                    startActivity(intent)
                }
            }
        }

        vm.accountIdRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding?.let { binding ->
                    binding.userId.text = it.toString()
                    binding.error.root.isVisible = false
                    binding.mainContent.isVisible = true
                }
            }


        vm.errorRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding?.let { binding ->
                    binding.error.root.isVisible = true
                    binding.mainContent.isVisible = false
                }
            }
    }

    private fun checkIsSberbankOnlineInstalled(): Boolean {
        val SBERBANK_HOST = "ru.sberbankmobile"
        val SBERBANK_ACTIVITY = "ru.sberbank.mobile.external.SberbankLoginActivity"
        return Intent()
            .apply {
                component = ComponentName(SBERBANK_HOST, SBERBANK_ACTIVITY)
            }
            .run {
                requireContext().packageManager
                    .resolveActivity(this, PackageManager.MATCH_DEFAULT_ONLY) != null
            }
    }


}