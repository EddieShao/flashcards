package com.example.flashcards.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.data.entities.Card
import com.example.flashcards.views.FlashCard

data class ProgressCard(
    val data: Card,
    var isHappy: Boolean
)

private object DiffCallback : DiffUtil.ItemCallback<ProgressCard>() {
    override fun areItemsTheSame(oldItem: ProgressCard, newItem: ProgressCard): Boolean {
        return when {
            oldItem.data.id == null && newItem.data.id == null -> {
                oldItem.data.hashCode() == newItem.data.hashCode()
            }
            oldItem.data.id != null && newItem.data.id != null -> {
                oldItem.data.id == newItem.data.id
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: ProgressCard, newItem: ProgressCard): Boolean {
        return oldItem == newItem
    }
}

class CardAdapter(
    private val showFlip: Boolean = false,
    private val showDelete: Boolean = false,
    private val showFace: Boolean = false,
    private val onDelete: ((card: ProgressCard) -> Unit)? = null,
    private val onTextChanged: ((side: FlashCard.Side, newText: String, card: ProgressCard) -> Unit)? = null
) : ListAdapter<ProgressCard, CardAdapter.ViewHolder>(DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        FlashCard(parent.context).also { card ->
            card.showFlip = showFlip
            card.showDelete = showDelete
            card.showFace = showFace
        }
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun addCard(card: ProgressCard, index: Int) {
        submitList(currentList.toMutableList().apply { add(index, card) })
    }

    fun removeCard(card: ProgressCard) {
        submitList(currentList.toMutableList().apply { remove(card) })
    }

    inner class ViewHolder(val view: FlashCard) : RecyclerView.ViewHolder(view) {
        fun bind(card: ProgressCard) {
            with(view) {
                front = card.data.front
                back = card.data.back
                isHappy = card.isHappy
                onDelete = { view ->
                    this@CardAdapter.onDelete?.invoke(card)
                }
                onTextChanged = { side, text ->
                    this@CardAdapter.onTextChanged?.invoke(side, text, card)
                }
            }
        }
    }
}
