package com.example.flashcards.models

import java.util.Calendar

data class CardModel(
    var front: String,
    var back: String,
    var isHappy: Boolean,
    val createdOn: Long = Calendar.getInstance().timeInMillis,
    val id: Int? = null
)