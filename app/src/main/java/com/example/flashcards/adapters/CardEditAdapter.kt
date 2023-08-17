package com.example.flashcards.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.data.entities.Card
import com.example.flashcards.databinding.CardBinding
import com.example.flashcards.helpers.SystemHelper
import kotlinx.coroutines.launch

class CardEditAdapter(private val cards: MutableList<Card>) :
    RecyclerView.Adapter<CardEditAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        CardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.front.run {
            text.onFocusChangeListener = SystemHelper.hideKeypadListener
            text.setText(cards[position].front)
            text.doOnTextChanged { text, start, before, count ->
                // TODO: update callback
            }
            flipToBack.setOnClickListener { button ->
                flip(holder.binding.front.root, holder.binding.back.root)
            }
            delete.setOnClickListener {
                // TODO: delete callback
            }
        }
        holder.binding.back.run {
            root.visibility = View.INVISIBLE
            text.onFocusChangeListener = SystemHelper.hideKeypadListener
            text.setText(cards[position].back)
            text.doOnTextChanged { text, start, before, count ->
                // TODO: update callback
            }
            flipToFront.setOnClickListener { button ->
                flip(holder.binding.back.root, holder.binding.front.root)
            }
            delete.setOnClickListener { button ->
                // TODO: delete callback
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(newCards: Collection<Card>) {
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
            from.visibility = View.INVISIBLE
        }

        flipToBack.start()
        flipToFront.start()
    }
}