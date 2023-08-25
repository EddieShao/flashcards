package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.models.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val initTitle = MutableLiveData<String>()
    val initCards = MutableLiveData<List<CardModel>>()

    init {
        stackId?.let { stackId ->
            viewModelScope.launch(Dispatchers.IO) {
                val (stack, cards) = Database.instance.stackDao().loadStackAndCards(stackId).entries.first()
                val cardModels = cards.map { card ->
                    CardModel(card.front, card.back, isHappy = false, card.createdOn, card.id)
                }
                initTitle.postValue(stack.title)
                initCards.postValue(cardModels)
            }
        } ?: run {
            initTitle.postValue("")
            initCards.postValue(mutableListOf())
        }
    }

    fun isDirty(title: String, cards: List<CardModel>): Boolean {
        val initCards = initCards.value.orEmpty()
        val initTitle = initTitle.value.orEmpty()

        if (title != initTitle || cards.size != initCards.size) {
            return true
        }

        val sortedInitCards = initCards.sortedBy { it.hashCode() }
        val sortedCards = cards.sortedBy { it.hashCode() }
        for (i in sortedCards.indices) {
            if (sortedCards[i] != sortedInitCards[i]) {
                return true
            }
        }
        return false
    }

    fun createCard() = CardModel(front = "", back = "", isHappy = false)

    fun save(title: String, cards: List<CardModel>) {
        // TODO
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val stackId: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(stackId) as T
        }
    }
}
