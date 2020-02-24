package com.vicky7230.tasker.utils

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object AnimUtils {

    fun slideView(view: View, currentHeight: Int, newHeight: Int) {
        val slideAnimator = ValueAnimator
            .ofInt(currentHeight, newHeight)
            .setDuration(1500)

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */
        slideAnimator.addUpdateListener { valueAnimator: ValueAnimator ->
            val value: Int = valueAnimator.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }

        /*  We use an animationSet to play the animation  */
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.play(slideAnimator)
        animationSet.start()
    }
}