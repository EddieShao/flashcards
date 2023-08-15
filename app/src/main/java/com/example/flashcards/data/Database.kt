package com.example.flashcards.data

import android.content.Context
import androidx.room.Database as DatabaseAnnotation
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flashcards.data.daos.CardDao
import com.example.flashcards.data.daos.StackDao
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack

@DatabaseAnnotation(entities = [Card::class, Stack::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun stackDao(): StackDao

    companion object {
        private var _instance: Database? = null

        val instance: Database get() = synchronized(this) { _instance!! }

        fun init(ctx: Context) {
            // assert because it's crucial to not memory leak db
            assert(_instance == null) { "Database is already initialized" }
            _instance = Room.databaseBuilder(ctx, Database::class.java, "database").build()
        }

        fun destroy() {
            _instance?.run { if (isOpen) close() }
            _instance = null
        }
    }
}
