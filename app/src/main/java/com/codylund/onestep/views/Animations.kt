package com.codylund.onestep.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import java.time.Duration

object Animations {
    /**
     * Animate Step status on tap.
     */
    fun clickStepStatus(view: View) {
        // Prepare the scaling animations on each dimension
        val upY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f, 1.3f).setDuration(100)
        val upX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f, 1.3f).setDuration(100)
        val downY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.3f, 1.0f).setDuration(300)
        val downX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.3f, 1.0f).setDuration(300)

        // Play the scale up animations together
        val scaleUp = AnimatorSet()
        scaleUp.playTogether(upX, upY)

        // Play the scale down animations together
        val scaleDown = AnimatorSet()
        scaleDown.playTogether(downX, downY)

        // Play the scale up and scale down animation sets together
        val playSequence = AnimatorSet()
        playSequence.playSequentially(scaleUp, scaleDown)

        playSequence.start()
    }

    /**
     * Fade the view to the target alpha with the given delay.
     */
    fun fadeWithDelay(view: View, delay: Long, duration: Long = 300, alpha: Float) {
        view.animate()
                .setStartDelay(delay)
                .setDuration(duration)
                .alpha(alpha)
                .start()
    }
}