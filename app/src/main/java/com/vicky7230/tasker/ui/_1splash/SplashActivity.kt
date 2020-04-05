package com.vicky7230.tasker.ui._1splash

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._2login.LoginActivity
import com.vicky7230.tasker.ui._4home.HomeActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splashViewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]

        init()
    }

    private fun init() {

        splashViewModel.isUserLoggedIn.observe(this, Observer {
            if (it) {
                lifecycleScope.launch {
                    delay(1500)
                    startActivity(HomeActivity.getStartIntent(this@SplashActivity))
                    finish()
                }
            } else {
                lifecycleScope.launch {
                    delay(1500)
                    startActivity(LoginActivity.getStartIntent(this@SplashActivity))
                    finish()
                }
            }
        })

        splashViewModel.isUserLoggedIn()


    }
}
