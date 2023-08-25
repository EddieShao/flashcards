package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.models.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val initStack = MutableLiveData<Stack>()
    val initCards = MutableLiveData<List<CardModel>>()

    init {
        stackId?.let { stackId ->
            viewModelScope.launch(Dispatchers.IO) {
                val (stack, cards) = Database.instance.stackDao().loadStackAndCards(stackId).entries.first()
                val cardModels = cards.map { card ->
                    CardModel(card.front, card.back, isHappy = false, card.createdOn, card.id)
                }
                initStack.postValue(stack)
                initCards.postValue(cardModels)
            }
        }
    }

    fun isDirty(title: String, cards: List<CardModel>): Boolean {
        val initCards = initCards.value.orEmpty()
        val initTitle = initStack.value?.title.orEmpty()

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
        if (!isDirty(title, cards)) return

        val stackDao = Database.instance.stackDao()
        val cardDao = Database.instance.cardDao()
        viewModelScope.launch(Dispatchers.IO) {
            val stackId = stackId?.also {
                stackDao.updateStacks(Stack(title, initStack.value!!.createdOn, it))
            } ?: stackDao.insertStack(Stack(title)).toInt()

            // toSet() call is safe because createdOn field is (realistically) unique
            val toDelete = initCards.value.orEmpty().minus(cards.toSet())
            val (toCreate, toUpdate) = cards.partition { card -> card.id == null }

            cardDao.deleteCards(*toDelete.map { it.toCard(stackId) }.toTypedArray())
            cardDao.insertCards(*toCreate.map { it.toCard(stackId) }.toTypedArray())
            cardDao.updateCards(*toUpdate.map { it.toCard(stackId) }.toTypedArray())
        }
    }

    private fun CardModel.toCard(stackId: Int) = Card(front, back, stackId, createdOn, id ?: 0)

    @Suppress("UNCHECKED_CAST")
    class Factory(private val stackId: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(stackId) as T
        }
    }
}
