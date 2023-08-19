package com.example.flashcards.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.data.entities.Card
import com.example.flashcards.databinding.CardBinding
import com.example.flashcards.helpers.SystemHelper

class CardAdapter(
    private val cards: MutableList<Pair<Card, Boolean>>, // boolean: 0 == sad, 1 == happy
    private val showFlip: Boolean = false,
    private val showDelete: Boolean = false,
    private val showFace: Boolean = false
) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        CardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ctx = holder.binding.root.context
        holder.binding.front.run {
            root.background = AppCompatResources.getDrawable(ctx, R.drawable.card)
            flip.run {
                if (showFlip) {
                    background.setTint(ContextCompat.getColor(ctx, R.color.background))
                    setImageResource(R.drawable.flip_to_back_30)
                    contentDescription = "Flip to back side"
                    setOnClickListener { button ->
                        flip(holder.binding.front.root, holder.binding.back.root)
                    }
                } else {
                    visibility = View.GONE
                }
            }
            delete.run {
                if (showDelete) {
                    background.setTint(ContextCompat.getColor(ctx, R.color.background))
                    contentDescription = "Delete card"
                    setOnClickListener { button ->
                        // TODO: delete callback
                    }
                } else {
                    visibility = View.GONE
                }
            }
            spacerText.run {
                textSize = 16f
                setText(cards[position].first.back)
            }
            editText.run {
                textSize = 20f
                setText(cards[position].first.front)
                onFocusChangeListener = SystemHelper.hideKeypadListener
                doOnTextChanged { text, start, before, count ->
                    // TODO: text changed callback
                    holder.binding.back.spacerText.setText(text)
                }
            }
            face.run {
                if (showFace) {
                    val correct = cards[position].second
                    setImageResource(if (correct) R.drawable.happy_30 else R.drawable.sad_30)
                    setColorFilter(
                        ContextCompat.getColor(ctx, if (correct) R.color.green else R.color.red),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    visibility = View.INVISIBLE
                }
            }
        }
        holder.binding.back.run {
            root.background = AppCompatResources.getDrawable(ctx, R.drawable.card_tinted)
            flip.run {
                if (showFlip) {
                    background.setTint(ContextCompat.getColor(ctx, R.color.background_tinted))
                    setImageResource(R.drawable.flip_to_front_30)
                    contentDescription = "Flip to front side"
                    setOnClickListener { button ->
                        flip(holder.binding.back.root, holder.binding.front.root)
                    }
                } else {
                    visibility = View.GONE
                }
            }
            delete.run {
                if (showDelete) {
                    background.setTint(ContextCompat.getColor(ctx, R.color.background_tinted))
                    contentDescription = "Delete card"
                    setOnClickListener { button ->
                        // TODO: delete callback
                    }
                } else {
                    visibility = View.GONE
                }
            }
            spacerText.run {
                textSize = 20f
                setText(cards[position].first.front)
            }
            editText.run {
                textSize = 16f
                setText(cards[position].first.back)
                onFocusChangeListener = SystemHelper.hideKeypadListener
                doOnTextChanged { text, start, before, count ->
                    // TODO: text changed callback
                    holder.binding.front.spacerText.setText(text)
                }
            }
            face.run {
                if (showFace) {
                    val correct = cards[position].second
                    setImageResource(if (correct) R.drawable.happy_30 else R.drawable.sad_30)
                    setColorFilter(
                        ContextCompat.getColor(ctx, if (correct) R.color.green else R.color.red),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    visibility = View.INVISIBLE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(newCards: Collection<Pair<Card, Boolean>>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    private fun flip(from: View, to: View) {
        to.visibility = View.VISIBLE

        // zoom camera out so flip animation doesn't clip outside of margins
        (8000 * from.context.resources.displayMetrics.density).let { cameraDistance ->
            from.cameraDistance = cameraDistance
            to.cameraDistance = cameraDistance
        }

        val flipToBack = AnimatorInflater.loadAnimator(from.context, R.animator.flip_to_back) as AnimatorSet
        flipToBack.setTarget(from)

        val flipToFront = AnimatorInflater.loadAnimator(to.context, R.animator.flip_to_front) as AnimatorSet
        flipToFront.setTarget(to)

        flipToFront.doOnEnd {
            from.visibility = View.GONE
        }

        flipToBack.start()
        flipToFront.start()
    }
}