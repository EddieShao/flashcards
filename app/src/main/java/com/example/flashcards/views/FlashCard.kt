package com.example.flashcards.views

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import androidx.core.animation.doOnEnd
import com.example.flashcards.R

// TODO: create custom Card view and add flip function to it

fun View.flip(to: View) {
    to.visibility = View.VISIBLE

    // zoom camera out so flip animation doesn't clip outside of margins
    (8000 * context.resources.displayMetrics.density).let { cameraDistance ->
        this.cameraDistance = cameraDistance
        to.cameraDistance = cameraDistance
    }

    val flipToBack = AnimatorInflater.loadAnimator(context, R.animator.flip_to_back) as AnimatorSet
    flipToBack.setTarget(this)

    val flipToFront = AnimatorInflater.loadAnimator(to.context, R.animator.flip_to_front) as AnimatorSet
    flipToFront.setTarget(to)

    flipToFront.doOnEnd {
        visibility = View.GONE
    }

    flipToBack.start()
    flipToFront.start()
}
