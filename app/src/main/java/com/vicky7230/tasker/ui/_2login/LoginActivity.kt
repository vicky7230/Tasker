package com.vicky7230.tasker.ui._2login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._3verifyOTP.VerifyOtpActivity
import com.vicky7230.tasker.utils.CommonUtils
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

        fun getStartIntentNewTask(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun getViewModel(): LoginViewModel {
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
        return loginViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {

        loginViewModel.resource.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    hideLoading()
                    startActivity(
                        VerifyOtpActivity.getStartIntent(
                            this,
                            email_edit_text.text.toString()
                        )
                    )
                }
            }
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
    }
}
