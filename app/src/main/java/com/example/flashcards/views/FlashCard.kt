package com.example.flashcards.views

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.flashcards.R
import com.example.flashcards.databinding.CardBinding
import com.example.flashcards.helpers.SystemHelper

class FlashCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    companion object {
        const val WRAP_CONTENT = "wrap_content"
        const val MATCH_PARENT = "match_parent"
    }

    enum class Side { FRONT, BACK }

    private val binding =
        CardBinding.inflate(LayoutInflater.from(context), this, true)

    var onDelete: ((view: View) -> Unit)? = null

    var onTextChanged: ((side: Side, text: String) -> Unit)? = null

    var showFlip = false
        set(value) {
            field = value
            val visibility = if (value) View.VISIBLE else View.GONE
            binding.front.flip.visibility = visibility
            binding.back.flip.visibility = visibility
        }

    var showDelete = false
        set(value) {
            field = value
            val visibility = if (value) View.VISIBLE else View.GONE
            binding.front.delete.visibility = visibility
            binding.back.delete.visibility = visibility
        }

    var showFace = false
        set(value) {
            field = value
            val visibility = if (value) View.VISIBLE else View.GONE
            binding.front.face.visibility = visibility
            binding.back.face.visibility = visibility
        }

    var front = ""
        set(value) {
            field = value
            binding.front.editText.setText(value)
            binding.back.spacerText.setText(value)
        }

    var back = ""
        set(value) {
            field = value
            binding.front.spacerText.setText(value)
            binding.back.editText.setText(value)
        }

    var isHappy = false
        set(value) {
            field = value
            for (side in listOf(binding.front, binding.back)) {
                side.face.setImageResource(if (value) R.drawable.happy_30 else R.drawable.sad_30)
                side.face.setColorFilter(
                    ContextCompat.getColor(context, if (value) R.color.green else R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

    var editable = true
        set(value) {
            field = value
            for (side in listOf(binding.front, binding.back)) {
                side.editText.isEnabled = value
                side.editText.isClickable = value
                side.editText.isLongClickable = value
            }
        }

    var innerHeight = WRAP_CONTENT
        set(value) {
            field = value
            val valueNum = when (value) {
                WRAP_CONTENT -> LayoutParams.WRAP_CONTENT
                MATCH_PARENT -> LayoutParams.MATCH_PARENT
                else -> throw Exception("Invalid layout height: $value")
            }
            binding.root.layoutParams.height = valueNum
            for (side in listOf(binding.front, binding.back)) {
                side.root.layoutParams.height = valueNum
                side.spacerText.layoutParams.height = valueNum
                side.editText.layoutParams.height = valueNum
            }
        }

    init {
        val styledAttrs =
            context.theme.obtainStyledAttributes(attrs, R.styleable.FlashCardView, 0, 0)

        showFlip = styledAttrs.getBoolean(R.styleable.FlashCardView_show_flip, false)
        showDelete = styledAttrs.getBoolean(R.styleable.FlashCardView_show_delete, false)
        showFace = styledAttrs.getBoolean(R.styleable.FlashCardView_show_face, false)
        editable = styledAttrs.getBoolean(R.styleable.FlashCardView_editable, true)
        innerHeight = styledAttrs.getString(R.styleable.FlashCardView_inner_height) ?: WRAP_CONTENT

        for (side in listOf(Side.FRONT, Side.BACK)) {
            fun <T> bySide(front: T, back: T) = when (side) {
                Side.FRONT -> front
                Side.BACK -> back
            }

            val cardSide = bySide(binding.front, binding.back)

            cardSide.root.background = AppCompatResources.getDrawable(
                context,
                bySide(R.drawable.card, R.drawable.card_tinted)
            )

            with(cardSide.flip) {
                setImageResource(
                    bySide(R.drawable.flip_to_back_30, R.drawable.flip_to_front_30)
                )
                background.setTint(
                    ContextCompat.getColor(
                        context,
                        bySide(R.color.background, R.color.background_tinted)
                    )
                )
                contentDescription = "Flip to ${bySide("front", "back")} side"
                setOnClickListener { flip() }
            }

            with(cardSide.delete) {
                background.setTint(
                    ContextCompat.getColor(
                        context,
                        bySide(R.color.background, R.color.background_tinted)
                    )
                )
                contentDescription = "Delete card"
                setOnClickListener { view -> onDelete?.invoke(view) }
            }

            with(cardSide.editText) {
                onFocusChangeListener = SystemHelper.hideKeypadListener
                textSize = bySide(20f, 16f)
                doOnTextChanged { text, start, before, count ->
                    bySide(binding.back, binding.front).spacerText.setText(text)
                    onTextChanged?.invoke(bySide(Side.FRONT, Side.BACK), text.toString())
                }
            }

            with(cardSide.spacerText) {
                textSize = bySide(16f, 20f)
            }
        }
    }

    private fun flip() {
        val from = if (binding.front.root.isVisible) binding.front.root else binding.back.root
        val to = if (binding.front.root.isVisible) binding.back.root else binding.front.root

        to.visibility = View.VISIBLE

        // zoom camera out so flip animation doesn't clip outside of margins
        (8000 * context.resources.displayMetrics.density).let { cameraDistance ->
            from.cameraDistance = cameraDistance
            to.cameraDistance = cameraDistance
        }

        val flipToBack = AnimatorInflater.loadAnimator(context, R.animator.flip_to_back) as AnimatorSet
        flipToBack.setTarget(from)

        val flipToFront = AnimatorInflater.loadAnimator(context, R.animator.flip_to_front) as AnimatorSet
        flipToFront.setTarget(to)

        flipToFront.doOnEnd {
            from.visibility = View.GONE
        }

        flipToBack.start()
        flipToFront.start()
    }
}
