package com.vicky7230.tasker.ui.splash

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.ui.home.HomeActivity
import com.vicky7230.tasker.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        init()
    }

    private fun init() {
        lifecycleScope.launch {
            delay(2000)
            startActivity(LoginActivity.getStartIntent(this@SplashActivity))
            finish()
        }
    }
}
