package com.example.flashcards.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Stack(
    val title: String,

    @ColumnInfo(name = "created_on")
    val createdOn: Long = Calendar.getInstance().timeInMillis,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
