package com.example.flashcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.helpers.ProgressManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishViewModel : ViewModel() {
    val numCorrect get() = ProgressManager.instance.cards.count { it.isHappy }
    val numIncorrect get() = ProgressManager.instance.cards.count { !it.isHappy }

    fun retry() {
        viewModelScope.launch(Dispatchers.IO) {
            ProgressManager.instance.retry()
        }
    }
}