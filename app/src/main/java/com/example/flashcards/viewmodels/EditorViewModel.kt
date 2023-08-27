package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.models.CardModel
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val initStack = MutableLiveData<Stack>()
    val initCards = MutableLiveData<List<CardModel>>()

    var title = ""
    private val _cards = mutableListOf<CardModel>()
    val cards get() = _cards.toList()

    val isDirty: Boolean get() {
        val initCards = initCards.value.orEmpty()
        val initTitle = initStack.value?.title.orEmpty()

        if (title != initTitle || _cards.size != initCards.size) {
            return true
        }

        val sortedInitCards = initCards.sortedBy { it.hashCode() }
        val sortedCards = _cards.sortedBy { it.hashCode() }
        for (i in sortedCards.indices) {
            if (sortedCards[i] != sortedInitCards[i]) {
                return true
            }
        }
        return false
    }

    init {
        stackId?.let { stackId ->
            viewModelScope.launch(Dispatchers.IO) {
                val (dbStack, dbCards) = Database.instance.stackDao().loadStackAndCards(stackId).entries.first()
                val cardModels = dbCards.map { card ->
                    CardModel(card.front, card.back, isHappy = false, card.createdOn, card.id)
                }

                initStack.postValue(dbStack)
                initCards.postValue(cardModels)

                title = dbStack.title
                _cards.clear()
                _cards.addAll(cardModels)
            }
        }
    }

    fun addCard(index: Int) = CardModel(front = "", back = "", isHappy = false).also { card ->
        _cards.add(index, card)
    }

    fun removeCard(index: Int) {
        _cards.removeAt(index)
    }

    fun updateCard(index: Int, side: FlashCard.Side, newText: String) {
        when (side) {
            FlashCard.Side.FRONT -> _cards[index].front = newText
            FlashCard.Side.BACK -> _cards[index].back = newText
        }
    }

    fun save() {
        if (!isDirty) return

        val stackDao = Database.instance.stackDao()
        val cardDao = Database.instance.cardDao()
        viewModelScope.launch(Dispatchers.IO) {
            val stackId = stackId?.also {
                stackDao.updateStacks(Stack(title, initStack.value!!.createdOn, it))
            } ?: stackDao.insertStack(Stack(title)).toInt()

            // toSet() call is safe because createdOn field is (realistically) unique
            val toDelete = initCards.value.orEmpty().minus(_cards.toSet())
            val (toCreate, toUpdate) = _cards.partition { card -> card.id == null }

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
