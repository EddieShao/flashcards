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
    suspend fun loadStackAndCardsOLD(stackId: Int): StackAndCards

    @Query(
        "SELECT * FROM stack " +
        "JOIN card ON stack.id = card.stack_id " +
        "WHERE stack.id = :stackId " +
        "ORDER BY card.created_on DESC"
    )
    suspend fun loadStackAndCards(stackId: Int): Map<Stack, List<Card>>

    @Query("SELECT * FROM stack WHERE title LIKE '%' || :query || '%' ORDER BY title COLLATE NOCASE ASC LIMIT :limit OFFSET :offset")
    suspend fun pageStacksByTitle(query: String, limit: Int, offset: Int): List<Stack>

    @Query("SELECT * FROM stack WHERE title LIKE '%' || :query || '%' ORDER BY created_on DESC LIMIT :limit OFFSET :offset")
    suspend fun pageStacksByCreatedOn(query: String, limit: Int, offset: Int): List<Stack>
}