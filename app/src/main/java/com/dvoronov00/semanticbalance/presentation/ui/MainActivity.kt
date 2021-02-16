package com.dvoronov00.semanticbalance.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.presentation.App
import com.dvoronov00.semanticbalance.presentation.ui.account.AccountFragment
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.github.terrakok.cicerone.androidx.FragmentScreen
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var navigatorHolder: NavigatorHolder


    private val navigator = object : AppNavigator(this, R.id.container){
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigatorHolder.setNavigator(navigator)
        router.newRootScreen(AccountFragment.screen())
    }
}


fun Fragment.toScreen(): Screen {
    return FragmentScreen{ this }
}
