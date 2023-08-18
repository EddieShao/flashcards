package com.example.flashcards.helpers

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

object ThemeHelper {
    @ColorInt
    fun getThemeColor(@AttrRes attrRes: Int, context: Context) = TypedValue()
        .apply { context.theme.resolveAttribute (attrRes, this, true) }
        .data
}