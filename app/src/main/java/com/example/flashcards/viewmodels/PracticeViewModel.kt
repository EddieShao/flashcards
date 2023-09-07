package com.example.flashcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.helpers.InProgress
import com.example.flashcards.helpers.ProgressManager
import com.example.flashcards.helpers.Status
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PracticeViewModel : ViewModel() {
    val madeProgress: Boolean get() {
        val status = ProgressManager.instance.status.value
        return status is InProgress && status.endIndex > 0
    }

    suspend fun watchStatus(action: suspend (Status) -> Unit) {
        ProgressManager.instance.status.collectLatest(action)
    }

    fun setVisibleSide(index: Int, side: FlashCard.Side) {
        ProgressManager.instance.setVisibleSide(index, side)
    }

    fun next(isHappy: Boolean? = null) {
        viewModelScope.launch(Dispatchers.Default) {
            ProgressManager.instance.next(isHappy)
        }
    }

    fun prev() {
        viewModelScope.launch(Dispatchers.Default) {
            ProgressManager.instance.prev()
        }
    }
}