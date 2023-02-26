package com.vicky7230.tasker.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun <CLS> Class<*>.findGenericWithType(targetClass: Class<*>): Class<out CLS>? {
    var currentType: Type? = this

    while (true) {
        val answerClass =
            (currentType as? ParameterizedType)?.actualTypeArguments
                ?.mapNotNull { it as? Class<*> }
                ?.findLast { targetClass.isAssignableFrom(it) }

        if (answerClass != null) {
            @Suppress("UNCHECKED_CAST")
            return answerClass as Class<out CLS>?
        }

        currentType = when (currentType) {
            is Class<*> -> currentType.genericSuperclass
            is ParameterizedType -> currentType.rawType
            else -> return null //or throw an exception
        }
    }
}

fun Activity.openLink(link: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(link)
        )
    )
}

fun LinearLayout.addOnGlobalLayoutListener(callback: (ViewTreeObserver.OnGlobalLayoutListener) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                callback.invoke(this)
            }
        })
}

fun View.gone(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}