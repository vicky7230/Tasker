package com.vicky7230.tasker.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui.base.BaseActivity

class LoginActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}
