package com.example.flashcards.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private var prefs: SharedPreferences? = null

    fun init(activity: Activity) {
        prefs = activity.getPreferences(Context.MODE_PRIVATE)
    }

    fun destroy() {
        prefs = null
    }

    fun getString(key: String, default: String) = prefs?.getString(key, default) ?: default

    fun putString(key: String, value: String) {
        prefs?.edit()?.run {
            putString(key, value)
            apply()
        }
    }
}