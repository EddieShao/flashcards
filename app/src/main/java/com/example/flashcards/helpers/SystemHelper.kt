package com.example.flashcards.helpers

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager

object SystemHelper {
    private var getService: ((name: String) -> Any)? = null

    fun init(activity: Activity) {
        getService = { name -> activity.getSystemService(name) }
    }

    fun destroy() {
        getService = null
    }

    fun hideKeypad(windowToken: IBinder) {
        val imm = getService?.invoke(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }
}