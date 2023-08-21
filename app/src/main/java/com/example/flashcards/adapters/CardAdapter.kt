package com.example.flashcards.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
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
import com.example.flashcards.views.Dialog

data class DisplayCard(
    var front: String,
    var back: String,
    val isHappy: Boolean,
    val data: Card? = null // null <=> this is a new card
)

data class CardAdapterState(
    val cards: List<DisplayCard>,
    val deletedCards: List<DisplayCard>
)

class CardAdapter(
    private val showFlip: Boolean = false,
    private val showDelete: Boolean = false,
    private val showFace: Boolean = false
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    private var initData = emptyList<DisplayCard>()

    private val cards = mutableListOf<DisplayCard>()
    private val deletedCards = mutableListOf<DisplayCard>()

    val state get() = CardAdapterState(cards, deletedCards)

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
                        showConfirmDeleteDialog(cards[position], root.context)
                    }
                } else {
                    visibility = View.GONE
                }
            }
            spacerText.run {
                textSize = 16f
                setText(cards[position].back)
            }
            editText.run {
                textSize = 20f
                setText(cards[position].front)
                onFocusChangeListener = SystemHelper.hideKeypadListener
                doOnTextChanged { text, start, before, count ->
                    cards[position].front = text.toString()
                    holder.binding.back.spacerText.setText(text)
                }
            }
            face.run {
                if (showFace) {
                    val isHappy = cards[position].isHappy
                    setImageResource(if (isHappy) R.drawable.happy_30 else R.drawable.sad_30)
                    setColorFilter(
                        ContextCompat.getColor(ctx, if (isHappy) R.color.green else R.color.red),
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
                        showConfirmDeleteDialog(cards[position], root.context)
                    }
                } else {
                    visibility = View.GONE
                }
            }
            spacerText.run {
                textSize = 20f
                setText(cards[position].front)
            }
            editText.run {
                textSize = 16f
                setText(cards[position].back)
                onFocusChangeListener = SystemHelper.hideKeypadListener
                doOnTextChanged { text, start, before, count ->
                    cards[position].back = text.toString()
                    holder.binding.front.spacerText.setText(text)
                }
            }
            face.run {
                if (showFace) {
                    val isHappy = cards[position].isHappy
                    setImageResource(if (isHappy) R.drawable.happy_30 else R.drawable.sad_30)
                    setColorFilter(
                        ContextCompat.getColor(ctx, if (isHappy) R.color.green else R.color.red),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                } else {
                    visibility = View.INVISIBLE
                }
            }
        }
    }

    fun isDirty(): Boolean {
        if (initData.size != cards.size) return true
        for (i in 0 until cards.size) {
            if (cards[i] != initData[i]) {
                return true
            }
        }
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(newCards: List<DisplayCard>) {
        initData = newCards.map { it.copy() }
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    fun insertCard(position: Int, card: DisplayCard) {
        cards.add(position, card)
        notifyItemInserted(position)
    }

    private fun deleteCard(card: DisplayCard) {
        val pos = cards.indexOf(card)
        val deleted = cards.removeAt(pos)
        deleted.data?.let { data -> deletedCards.add(deleted) }
        notifyItemRemoved(pos)
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

    private fun showConfirmDeleteDialog(card: DisplayCard, context: Context) {
        Dialog(context).run {
            setTitle("Delete Card")
            setMessage("Are you sure you want to delete this card?")
            setPositiveButton("Delete") { dialog, which ->
                deleteCard(card)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}