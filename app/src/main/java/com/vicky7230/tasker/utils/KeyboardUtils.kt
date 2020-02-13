package com.vicky7230.tasker.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

/**
 * Created by vicky on 10/10/17.
 */
object KeyboardUtils {
    fun hideSoftInput(activity: Activity) {
        var view = activity.currentFocus
        if (view == null) view = View(activity)
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSoftInput(
        appCompatEditText: AppCompatEditText,
        context: Context
    ) {
        appCompatEditText.isFocusable = true
        appCompatEditText.isFocusableInTouchMode = true
        appCompatEditText.requestFocus()
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(
            appCompatEditText,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    fun toggleSoftInput(context: Context) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}