package com.vicky7230.tasker.ui._1splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._2login.LoginActivity
import com.vicky7230.tasker.ui._4home.HomeActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    companion object {
        const val TOKEN_EXPIRED = "expired"
        const val TOKEN_ACTIVE = "active"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var splashViewModel: SplashViewModel

    override fun getViewModel(): SplashViewModel {
        splashViewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]
        return splashViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
    }

    private fun init() {

        retry_button.setOnClickListener {
            splashViewModel.refreshToken()
        }

        splashViewModel.tokenRefreshed.observe(this, Observer { tokenUpdated: Resource<Boolean> ->
            when (tokenUpdated) {
                is Resource.Loading -> {
                    retry_button.visibility = View.GONE
                    progress_bar.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    progress_bar.visibility = View.GONE
                    retry_button.visibility = View.VISIBLE
                    showError(tokenUpdated.exception.localizedMessage)
                }
                is Resource.Success -> {
                    progress_bar.visibility = View.GONE
                    if (tokenUpdated.data) {
                        startActivity(HomeActivity.getStartIntent(this@SplashActivity))
                        finish()
                    } else {
                        startActivity(LoginActivity.getStartIntent(this@SplashActivity))
                        finish()
                    }
                }
            }
        })

        splashViewModel.isUserLoggedIn.observe(this, Observer {
            if (it) {
                splashViewModel.refreshToken()
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
