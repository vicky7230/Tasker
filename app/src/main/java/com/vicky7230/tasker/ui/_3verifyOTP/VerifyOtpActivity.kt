package com.vicky7230.tasker.ui._3verifyOTP

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vicky7230.tasker.R

class VerifyOtpActivity : AppCompatActivity() {

    companion object {

        const val EXTRAS_EMAIL = "email"

        fun getStartIntent(context: Context, email: String): Intent {
            val intent = Intent(context, VerifyOtpActivity::class.java)
            intent.putExtra(EXTRAS_EMAIL, email)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
    }
}
