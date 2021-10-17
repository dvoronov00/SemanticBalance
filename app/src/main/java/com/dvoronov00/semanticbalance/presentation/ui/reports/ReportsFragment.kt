package com.dvoronov00.semanticbalance.presentation.ui.reports

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.databinding.FragmentReportsBinding
import com.dvoronov00.semanticbalance.domain.model.DataState
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.di.ViewModelFactory
import com.dvoronov00.semanticbalance.presentation.ui.reports.reportsAdapter.ReportsRecyclerAdapter
import com.dvoronov00.semanticbalance.presentation.ui.toScreen
import com.github.terrakok.cicerone.Screen
import com.tiper.MaterialSpinner
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ReportsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val vm: ReportsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ReportsViewModel::class.java)
    }

    private val reportsAdapter = ReportsRecyclerAdapter()

    override fun onAttach(context: Context) {
        App.getComponent().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentReportsBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        (activity as AppCompatActivity).setSupportActionBar(fragmentBinding.toolbar)
        return fragmentBinding.root
    }

    private val spinnerListener by lazy {
        object : MaterialSpinner.OnItemSelectedListener {
            override fun onItemSelected(
                parent: MaterialSpinner,
                view: View?,
                position: Int,
                id: Long
            ) {
                vm.cancelAll()
                when (position) {
                    0 -> vm.getPaymentsReports()
                    1 -> vm.getServicesReports()
                }
                parent.focusSearch(View.FOCUS_UP)?.requestFocus()
            }

            override fun onNothingSelected(parent: MaterialSpinner) {}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUX()
        initVM()
    }

    private fun initUI() {
        binding.recyclerReports.adapter = reportsAdapter

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.reports,
            android.R.layout.simple_spinner_item
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(binding.reportSpinner) {
            adapter = spinnerAdapter
            binding.reportSpinner.onItemSelectedListener = spinnerListener
            binding.reportSpinner.selection = 0

        }
    }

    private fun initUX() {
        binding.toolbar.setNavigationOnClickListener {
            vm.back()
        }
    }

    private fun initVM() {
        vm.reportsDataRelay
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is DataState.Loading -> {
                        binding.reportsShimmer.isVisible = true
                        binding.reportsError.root.isVisible = false
                        reportsAdapter.clearList()
                    }
                    is DataState.Success -> {
                        reportsAdapter.setList(it.data)
                        binding.reportsShimmer.isVisible = false
                        binding.reportsError.root.isVisible = false
                    }
                    is DataState.Failure -> {
                        binding.reportsShimmer.isVisible = false
                        binding.reportsError.root.isVisible = true
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        vm.onFragmentStart()
    }

    companion object {
        fun screen(): Screen {
            return ReportsFragment().toScreen()
        }
    }
}
