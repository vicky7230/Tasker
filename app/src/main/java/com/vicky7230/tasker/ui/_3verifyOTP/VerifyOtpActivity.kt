package com.vicky7230.tasker.ui._3verifyOTP

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._2login.LoginViewModel
import com.vicky7230.tasker.ui.home.HomeActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_verify_otp.*
import javax.inject.Inject

class VerifyOtpActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var verifyOtpViewModel: VerifyOtpViewModel

    companion object {

        const val EXTRAS_EMAIL = "email"

        fun getStartIntent(context: Context, email: String): Intent {
            val intent = Intent(context, VerifyOtpActivity::class.java)
            intent.putExtra(EXTRAS_EMAIL, email)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        verifyOtpViewModel =
            ViewModelProvider(this, viewModelFactory)[VerifyOtpViewModel::class.java]

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        verifyOtpViewModel.loading.observe(this, Observer {
            if (it)
                showLoading()
            else
                hideLoading()
        })

        verifyOtpViewModel.error.observe(this, Observer {
            showError(it)
        })

        verifyOtpViewModel.otpVerified.observe(this, Observer {
            if (it)
                startActivity(HomeActivity.getStartIntent(this@VerifyOtpActivity))
        })

        if (intent != null && intent.getStringExtra(EXTRAS_EMAIL) != null)
            verification_text.text =
                "Please type the verification code sent to ${intent.getStringExtra(EXTRAS_EMAIL)}"

        back_button.setOnClickListener { finish() }

        otp_view.requestFocus()

        otp_view.setOtpCompletionListener { otp ->
            if (intent != null && intent.getStringExtra(EXTRAS_EMAIL) != null)
                verifyOtpViewModel.verifyOtp(intent.getStringExtra(EXTRAS_EMAIL), otp)
        }
    }
}
