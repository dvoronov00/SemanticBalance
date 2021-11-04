package com.dvoronov00.semanticbalance.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.databinding.ActivityAuthBinding
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var storageRepository: StorageRepository

    @Inject
    lateinit var authRepository: AuthRepository

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    private val disposeBag = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getComponent().inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initUX()
    }

    private fun initUX() {
        binding.buttonEnter.setOnClickListener {
            val username = binding.textInputEditTextUsername.text.toString()
            val password = binding.textInputEditTextPassword.text.toString()
            when {
                username.trim().isEmpty() -> {
                    showSnackBar("Имя пользователя не может быть пустым")
                }
                password.trim().isEmpty() -> {
                    showSnackBar("Пароль не может быть пустым")
                }
                else -> {
                    auth(username = username, password)
                }
            }
        }

        binding.textViewDontKnowCredentials.setOnClickListener {
            AuthHintDialogFragment().show(supportFragmentManager, "hint")
        }

    }

    private fun auth(username: String, password: String) {
        binding.progressBarEnter.isVisible = true
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
        authRepository.auth(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                binding.progressBarEnter.isVisible = false
            }
            .subscribe({
                storageRepository.saveUser(it)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }, {
                val message = if (it is UnknownHostException) {
                    getString(R.string.error_no_connection)
                } else it.message.toString()
                showSnackBar(message)
            }).let(disposeBag::add)

    }

    private fun showSnackBar(text: String) {
        val snack = Snackbar.make(binding.parentLayout, text, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))

        val snackView = snack.view
        val params = snackView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackView.layoutParams = params
        snack.show()
    }

    override fun onDestroy() {
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
        super.onDestroy()
    }

}