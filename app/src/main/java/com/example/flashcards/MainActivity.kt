package com.example.flashcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flashcards.data.Database
import com.example.flashcards.helpers.SettingsHelper
import com.example.flashcards.helpers.SystemHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Database.init(applicationContext)
        SystemHelper.init(this)
        SettingsHelper.init(this)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        super.onDestroy()
        Database.destroy()
        SystemHelper.destroy()
        SettingsHelper.destroy()
    }
}