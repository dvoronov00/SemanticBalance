package com.dvoronov00.semanticbalance.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.domain.repository.AuthRepository
import com.dvoronov00.semanticbalance.domain.repository.StorageRepository
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.ui.MainActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {
    private val TAG = "AuthActivity"


    @Inject
    lateinit var storageRepository: StorageRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var analytics: FirebaseAnalytics

    private lateinit var parentLayout: CoordinatorLayout
    private lateinit var usernameTV: TextInputEditText
    private lateinit var passwordTV: TextInputEditText
    private lateinit var dontKnowCredentialsTV: TextView
    private lateinit var enterButton: MaterialButton
    private lateinit var enterProgressBar: ProgressBar

    private val disposeBag = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        setListeners()
    }

    private fun initViews() {
        parentLayout = findViewById(R.id.parentLayout)
        usernameTV = findViewById(R.id.textInputEditTextUsername)
        passwordTV = findViewById(R.id.textInputEditTextPassword)
        dontKnowCredentialsTV = findViewById(R.id.textViewDontKnowCredentials)
        enterButton = findViewById(R.id.buttonEnter)
        enterProgressBar = findViewById(R.id.progressBarEnter)
    }

    private fun setListeners() {
        enterButton.setOnClickListener {
            val username = usernameTV.text.toString()
            val password = passwordTV.text.toString()
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

    }

    private fun auth(username: String, password: String) {
        enterProgressBar.visibility = View.VISIBLE
        disposeBag.forEach { it.dispose() }
        disposeBag.clear()
        val disposable = authRepository.auth(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                analytics.logEvent("user_login_success",  null)
                enterProgressBar.visibility = View.GONE
                storageRepository.saveUser(it)
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }, {
                enterProgressBar.visibility = View.GONE
                val message = if (it is UnknownHostException) {
                    getString(R.string.error_no_connection)
                } else {
                    it.message.toString()
                }
                showSnackBar(message)


            })

        disposeBag.add(disposable)
    }

    private fun showSnackBar(text: String) {
        val bundle = Bundle()
        bundle.putString("error", text)
        analytics.logEvent("user_login_error",  bundle)
        val snack = Snackbar.make(parentLayout, text, Snackbar.LENGTH_LONG)
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