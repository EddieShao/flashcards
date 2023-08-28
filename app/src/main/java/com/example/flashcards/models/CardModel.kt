package com.example.flashcards.models

import com.example.flashcards.views.FlashCard
import java.util.Calendar

data class CardModel(
    var front: String,
    var back: String,
    var isHappy: Boolean,
    var visibleSide: FlashCard.Side,
    val createdOn: Long = Calendar.getInstance().timeInMillis,
    val id: Int? = null
)