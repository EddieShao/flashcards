package com.example.flashcards.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class StackAndCards(
    @Embedded
    val stack: Stack,

    @Relation(parentColumn = "id", entityColumn = "stack_id")
    val cards: List<Card>
)
