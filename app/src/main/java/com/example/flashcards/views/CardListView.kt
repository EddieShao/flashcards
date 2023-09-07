package com.example.flashcards.views

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.flashcards.R
import com.example.flashcards.models.CardModel
import kotlin.math.max

class CardListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    private val showFlip: Boolean
    private val showDelete: Boolean
    private val showFace: Boolean
    private val editable: Boolean
    private val dividerSizeDp: Int

    private val itemCount get() = (childCount + 1) / 2

    var onDelete: ((position: Int) -> Unit)? = null
    var onTextChanged: ((side: FlashCard.Side, text: String, position: Int) -> Unit)? = null

    init {
        val styledAttrs =
            context.theme.obtainStyledAttributes(attrs, R.styleable.CardListView, 0, 0)

        showFlip = styledAttrs.getBoolean(R.styleable.CardListView_show_flip, false)
        showDelete = styledAttrs.getBoolean(R.styleable.CardListView_show_delete, false)
        showFace = styledAttrs.getBoolean(R.styleable.CardListView_show_face, false)
        editable = styledAttrs.getBoolean(R.styleable.CardListView_editable, true)
        dividerSizeDp = styledAttrs.getInt(R.styleable.CardListView_divider_size_dp, 0)
    }

    fun submitList(list: List<CardModel>) {
        removeAllViews()
        list.forEachIndexed { index, card ->
            addView(createCard(card))
            if (index < list.size - 1) {
                addView(divider())
            }
        }
    }

    fun add(position: Int, card: CardModel) {
        if (position > itemCount) {
            throw IndexOutOfBoundsException("Index: $position, Size: $itemCount")
        }

        val cardView = createCard(card)
        if (itemCount == 0) {
            addView(cardView)
        } else if (position == itemCount) {
            addView(divider())
            addView(cardView)
        } else {
            addView(divider(), position.toLayoutPosition())
            addView(cardView, position.toLayoutPosition())
        }
    }

    fun remove(position: Int) {
        if (position >= itemCount) {
            throw IndexOutOfBoundsException("Index: $position, Size: $itemCount")
        }

        if (itemCount == 1) {
            removeAllViews()
        } else {
            removeViews(max(0, position.toLayoutPosition() - 1), 2)
        }
    }

    private fun createCard(card: CardModel) =
        FlashCard(context).also { cardView ->
            cardView.showFlip = showFlip
            cardView.showDelete = showDelete
            cardView.showFace = showFace
            cardView.editable = editable
            cardView.onDelete = { view ->
                onDelete?.invoke(indexOfChild(cardView).toPosition())
            }
            cardView.onTextChanged = { side, text ->
                onTextChanged?.invoke(side, text, indexOfChild(cardView).toPosition())
            }
            with(cardView) {
                front = card.front
                back = card.back
                isHappy = card.isHappy
                visibleSide = FlashCard.Side.FRONT
            }
        }

    private fun divider() = View(context).apply {
        val size = dividerSizeDp.dpToPx()
        layoutParams = when (orientation) {
            VERTICAL -> {
                LayoutParams(0, size)
            }
            HORIZONTAL -> {
                LayoutParams(size, 0)
            }
            else -> {
                throw Exception("Invalid orientation for MutableListView: $orientation")
            }
        }
    }

    private fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toLayoutPosition() = this * 2

    private fun Int.toPosition() = this / 2
}
