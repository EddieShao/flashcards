package com.example.flashcards.views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.setPadding
import com.example.flashcards.databinding.SnackBarBinding
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout

class SnackBar(anchor: View, @Duration duration: Int) {
    private val snackBar: Snackbar
    private val binding: SnackBarBinding

    init {
        binding = SnackBarBinding.inflate(LayoutInflater.from(anchor.context)).also { binding ->
            snackBar = Snackbar.make(anchor, "", duration).apply {
                with(view as SnackbarLayout) {
                    setBackgroundColor(Color.TRANSPARENT)
                    setPadding(0)
                    addView(binding.root)
                }
            }
        }
    }

    fun setText(text: String) {
        binding.text.text = text
    }

    fun setAction(text: String, callback: (View) -> Unit) {
        with(binding.action) {
            visibility = View.VISIBLE
            this.text = text
            setOnClickListener { view -> callback(view) }
        }
    }

    fun show() {
        snackBar.show()
    }

    companion object {
        const val LENGTH_SHORT = Snackbar.LENGTH_SHORT
        const val LENGTH_LONG = Snackbar.LENGTH_LONG
        const val LENGTH_INDEFINITE = Snackbar.LENGTH_INDEFINITE
    }
}
