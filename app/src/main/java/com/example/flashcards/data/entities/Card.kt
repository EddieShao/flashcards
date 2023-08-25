package com.example.flashcards.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Stack::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("stack_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Card(
    val front: String,

    val back: String,

    @ColumnInfo(name = "stack_id")
    val stackId: Int,

    @ColumnInfo(name = "created_on")
    val createdOn: Long = Calendar.getInstance().timeInMillis,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
