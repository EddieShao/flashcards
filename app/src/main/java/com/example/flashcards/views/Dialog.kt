package com.example.flashcards.views

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import com.example.flashcards.databinding.AlertDialogButtonsBinding

class Dialog(context: Context?) {
    private val builder: AlertDialog.Builder
    private val binding: AlertDialogButtonsBinding

    private var positiveListener: DialogInterface.OnClickListener? = null
    private var negativeListener: DialogInterface.OnClickListener? = null

    init {
        builder = AlertDialog.Builder(context).also { builder ->
            binding = AlertDialogButtonsBinding.inflate(LayoutInflater.from(context))
            builder.setView(binding.root)
        }
    }

    fun setTitle(text: String) {
        builder.setTitle(text)
    }

    fun setMessage(text: String) {
        builder.setMessage(text)
    }

    fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener) {
        binding.positiveButton.text = text
        positiveListener = listener
    }

    fun setNegativeButton(text: String, listener: DialogInterface.OnClickListener) {
        binding.negativeButton.text = text
        negativeListener = listener
    }

    fun show() {
        val dialog = builder.create()
        positiveListener?.let { listener ->
            binding.positiveButton.setOnClickListener {
                listener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            }
        }
        negativeListener?.let { listener ->
            binding.negativeButton.setOnClickListener {
                listener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE)
            }
        }
        dialog.show()
    }
}