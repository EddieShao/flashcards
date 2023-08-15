package com.example.flashcards.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceDivider(
    private val sizeDp: Int,
    private val verticalPadding: Boolean
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (verticalPadding) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = sizeDp
            }
            outRect.bottom = sizeDp
        } else {
            if (parent.getChildAdapterPosition(view) > 0) {
                outRect.top = sizeDp
            }
        }
    }
}