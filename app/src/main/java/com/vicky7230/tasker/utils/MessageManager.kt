package com.vicky7230.tasker.utils

import android.app.Activity
import android.app.Dialog
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.vicky7230.tasker.R

class MessageManager(private val activity: Activity) {

    private var progressDialog: Dialog? = null

    private fun displayMessage(message: String) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun displayError(message: String) {
        val snackBar =
            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        snackBar.view.setBackgroundResource(R.drawable.curved_bg_error)
        snackBar.show()
    }

    fun showLoading() {
        hideLoading()
        progressDialog = CommonUtils.showLoadingDialog(activity)
    }

    fun hideLoading() {
        if (progressDialog != null) {
            if (progressDialog?.isShowing == true)
                progressDialog?.cancel()
        }
    }

    fun showMessage(message: String?) {
        if (message != null)
            displayMessage(message)
    }

    fun showError(message: String?) {
        if (message != null)
            displayError(message)
    }

    fun showToast(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }
}