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

        startActivity(HomeActivity.getStartIntent(this@SplashActivity))
        finish()

    }
}
