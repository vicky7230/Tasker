package com.vicky7230.tasker.ui._1splash

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._4home.HomeActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : BaseActivity() {

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
