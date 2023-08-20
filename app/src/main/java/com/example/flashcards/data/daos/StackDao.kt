package com.example.flashcards.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.data.entities.StackAndCards

@Dao
interface StackDao {
    @Insert
    suspend fun insertStack(stack: Stack): Long

    @Update
    suspend fun updateStacks(vararg stack: Stack)

    @Delete
    suspend fun deleteStacks(vararg stack: Stack)

    @Transaction
    @Query("SELECT * FROM stack WHERE id = :stackId")
    suspend fun loadStackAndCards(stackId: Int): StackAndCards

    @Query("SELECT * FROM stack WHERE title LIKE '%' || :query || '%' ORDER BY :order ASC LIMIT :limit OFFSET :offset")
    suspend fun pageStacks(query: String, order: String, limit: Int, offset: Int): List<Stack>
}