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
        private lateinit var _instance: Database
        val instance get() = synchronized(this) { _instance }

        fun init(context: Context) {
            if (::_instance.isInitialized) return
            _instance =
                Room.databaseBuilder(context, Database::class.java, "database").build()
        }
    }
}
