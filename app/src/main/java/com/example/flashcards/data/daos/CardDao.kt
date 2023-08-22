package com.example.flashcards.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.flashcards.data.entities.Card

@Dao
interface CardDao {
    @Insert
    suspend fun insertCards(vararg cards: Card)

    @Update
    suspend fun updateCards(vararg cards: Card)

    @Delete
    suspend fun deleteCards(vararg cards: Card)

    @Query("SELECT * FROM card WHERE stack_id = :stackId ORDER BY RAND()")
    suspend fun loadCardsWithStackId(stackId: Int): List<Card>
}