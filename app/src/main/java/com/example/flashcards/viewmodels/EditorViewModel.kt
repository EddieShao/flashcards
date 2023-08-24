package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val stack = MutableLiveData<Stack>()
    val cards = MutableLiveData<List<Card>>()

    private val cardsToDelete = mutableListOf<Card>()

    init {
        stackId?.let { stackId ->
            viewModelScope.launch(Dispatchers.IO) {
                val dao = Database.instance.stackDao()
                dao.loadStackAndCards(stackId).entries.firstOrNull()?.let { (stack, cards) ->
                    this@EditorViewModel.stack.postValue(stack)
                    this@EditorViewModel.cards.postValue(cards)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val stackId: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(stackId) as T
        }
    }
}
