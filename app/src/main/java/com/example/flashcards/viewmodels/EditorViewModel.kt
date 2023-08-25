package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.models.CardModel
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val initTitle = MutableLiveData<String>()
    val initCards = MutableLiveData<List<CardModel>>()

    private var title = ""
    private val cards = mutableListOf<CardModel>()
    private val cardsToDelete = mutableListOf<CardModel>()

    val dirty: Boolean get() {
        val initCards = initCards.value.orEmpty()
        val initTitle = initTitle.value.orEmpty()
        if (
            title != initTitle ||
            cards.size != initCards.size ||
            cardsToDelete.isNotEmpty()
        ) { return true }
        for (i in 0 until cards.size) {
            if (cards[i] != initCards[i]) {
                return true
            }
        }
        return false
    }

    init {
        stackId?.let { stackId ->
            viewModelScope.launch(Dispatchers.IO) {
                val (k, v) = Database.instance.stackDao().loadStackAndCards(stackId).entries.first()
                title = k.title
                initTitle.postValue(k.title)
                v.map { CardModel(it.front, it.back, isHappy = false, it.createdOn, it.id) }.let {
                    cards.clear()
                    cards.addAll(it)
                    initCards.postValue(it)
                }
            }
        } ?: run {
            initTitle.postValue("")
            initCards.postValue(mutableListOf())
        }
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    fun createCard() = CardModel(front = "", back = "", isHappy = false).also { card ->
        cards.add(card)
    }

    fun deleteCard(card: CardModel) {
        if (card.id != null) {
            cardsToDelete.add(card)
        }
        cards.remove(card)
    }

    fun updateCard(side: FlashCard.Side, newText: String, card: CardModel) {
        assert(cards.contains(card))
        when (side) {
            FlashCard.Side.FRONT -> card.front = newText
            FlashCard.Side.BACK -> card.back = newText
        }
    }

    fun save() {
        // TODO
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val stackId: Int?) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditorViewModel(stackId) as T
        }
    }
}
