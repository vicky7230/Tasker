package com.vicky7230.tasker.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.databinding.ActivitySplashBinding
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.ui.home.HomeActivity
import dagger.android.AndroidInjection

@SuppressLint("CustomSplashScreen")
class SplashActivity: BaseActivity<ActivitySplashBinding>() {

    val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onBindingCreated() {
        super.onBindingCreated()
        init()
    }

    private fun init() {
        startActivity(HomeActivity.getStartIntent(this@SplashActivity))
        finish()
    }

}
