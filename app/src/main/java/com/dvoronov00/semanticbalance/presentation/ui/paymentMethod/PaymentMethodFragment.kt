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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dvoronov00.semanticbalance.databinding.FragmentPaymentMethodBinding
import com.dvoronov00.semanticbalance.domain.RemoteConfigData
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.ui.toScreen
import com.github.terrakok.cicerone.Screen
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class PaymentMethodFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentPaymentMethodBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        (activity as AppCompatActivity).setSupportActionBar(fragmentBinding.toolbar)
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { vm.back() }

        vm.accountIdRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.userId.text = it.toString()
                binding.error.root.isVisible = false
                binding.mainContent.isVisible = true
            }

        vm.errorRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.error.root.isVisible = true
                binding.mainContent.isVisible = false
            }

        vm.remoteConfigRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { config ->
                setRemoteConfigData(config)
            }

        updateSberbankOnlineMethodState()
    }

    private fun updateSberbankOnlineMethodState() {
        if (checkIsSberbankOnlineInstalled()) {
            binding.sberCardView.isEnabled = true
            binding.sberCardView.alpha = 1.0f
        } else {
            binding.sberCardView.isEnabled = false
            binding.sberCardView.alpha = 0.55f
        }
    }

    private fun setRemoteConfigData(remoteConfigData: RemoteConfigData) {
        with(binding) {
            paymentMethodHint.text = remoteConfigData.payment.methodHintText

            sberCardView.setOnClickListener {
                startActivityWithUri(remoteConfigData.payment.sberDeeplink)
            }

            qiwiCardView.setOnClickListener {
                startActivityWithUri(remoteConfigData.payment.qiwiDeeplink)
            }

            bankCardView.setOnClickListener {
                startActivityWithUri(remoteConfigData.payment.bankcardDeeplink)
            }
        }
    }

    private fun startActivityWithUri(uri: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(uri)).let(::startActivity)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun screen(): Screen {
            return PaymentMethodFragment().toScreen()
        }
    }
}