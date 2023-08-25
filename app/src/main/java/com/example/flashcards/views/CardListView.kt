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
    private val _cards = mutableListOf<CardModel>()
    val cards get() = _cards.toList()

    private val showFlip: Boolean
    private val showDelete: Boolean
    private val showFace: Boolean
    private val editable: Boolean
    private val dividerSizeDp: Int

    var onDelete: ((card: CardModel, position: Int) -> Unit)? = null

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

        _cards.clear()
        _cards.addAll(list)
    }

    fun add(position: Int, card: CardModel) {
        if (position > _cards.size) {
            throw IndexOutOfBoundsException("Index: $position, Size: ${_cards.size}")
        }

        val cardView = createCard(card)
        if (_cards.isEmpty()) {
            addView(cardView)
        } else if (position == _cards.size) {
            addView(divider())
            addView(cardView)
        } else {
            addView(divider(), position.toLayoutPosition())
            addView(cardView, position.toLayoutPosition())
        }

        _cards.add(position, card)
    }

    fun remove(position: Int) {
        if (position >= _cards.size) {
            throw IndexOutOfBoundsException("Index: $position, Size: ${_cards.size}")
        }

        if (_cards.size == 1) {
            removeAllViews()
        } else {
            removeViews(max(0, position.toLayoutPosition() - 1), 2)
        }

        _cards.removeAt(position)
    }

    private fun createCard(card: CardModel) =
        FlashCard(context).also { cardView ->
            cardView.showFlip = showFlip
            cardView.showDelete = showDelete
            cardView.showFace = showFace
            cardView.editable = editable

            with(cardView) {
                front = card.front
                back = card.back
                isHappy = card.isHappy
                onDelete = { view ->
                    this@CardListView.onDelete?.invoke(card, _cards.indexOf(card))
                }
                onTextChanged = { side, text ->
                    when (side) {
                        FlashCard.Side.FRONT -> card.front = text
                        FlashCard.Side.BACK -> card.back = text
                    }
                }
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
}
