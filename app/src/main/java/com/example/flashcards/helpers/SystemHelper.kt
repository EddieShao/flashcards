package com.example.flashcards.helpers

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

object SystemHelper {
    private lateinit var getService: (name: String) -> Any

    val Float.dp get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)

    val hideKeypadListener = View.OnFocusChangeListener { view, hasFocus ->
        if (!hasFocus) {
            hideKeypad(view.windowToken)
        }
    }

    fun init(activity: Activity) {
        getService = { name -> activity.getSystemService(name) }
    }

    fun showKeypad(target: View) {
        val imm = getService.invoke(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeypad(windowToken: IBinder) {
        val imm = getService.invoke(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}