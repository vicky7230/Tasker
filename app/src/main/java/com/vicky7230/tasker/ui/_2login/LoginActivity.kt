package com.vicky7230.tasker.ui._2login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._3verifyOTP.VerifyOtpActivity
import com.vicky7230.tasker.utils.CommonUtils
import com.vicky7230.tasker.utils.KeyboardUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var loginViewModel: LoginViewModel

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        init()
    }

    private fun init() {

        loginViewModel.loading.observe(this, Observer {
            if (it)
                showLoading()
            else
                hideLoading()
        })

        loginViewModel.error.observe(this, Observer {
            showError(it)
        })

        email_edit_text.requestFocus()

        send_otp.setOnClickListener {
            if (!TextUtils.isEmpty(email_edit_text.text) &&
                CommonUtils.isEmailValid(email_edit_text.text.toString())
            ) {
                loginViewModel.generateOTP(email_edit_text.text.toString())
            } else {
                showError("Invalid Email")
            }
        }

        loginViewModel.otpGenerated.observe(this, Observer {
            if (it)
                startActivity(
                    VerifyOtpActivity.getStartIntent(
                        this,
                        email_edit_text.text.toString()
                    )
                )
        })
    }
}
