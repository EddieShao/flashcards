package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel(private val stackId: Int?) : ViewModel() {
    val initStack = MutableLiveData<Stack>()
    val initCards = MutableLiveData<List<Card>>()

    private var title = ""
    private val cards = mutableListOf<Card>()
    private val cardsToDelete = mutableListOf<Card>()

    val dirty: Boolean get() {
        val initCards = initCards.value.orEmpty()
        val initTitle = initStack.value?.title.orEmpty()
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
                val (stack, cards) = Database.instance.stackDao().loadStackAndCards(stackId).entries.first()
                title = stack.title
                this@EditorViewModel.cards.clear()
                this@EditorViewModel.cards.addAll(cards)
                initStack.postValue(stack)
                initCards.postValue(cards.toMutableList())
            }
        } ?: run {
            initStack.postValue(Stack(""))
            initCards.postValue(mutableListOf())
        }
    }

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    fun createCard() = Card("", "").also { card -> cards.add(card) }

    fun deleteCard(card: Card) {
        if (card.id != null) {
            cardsToDelete.add(card)
        }
        cards.remove(card)
    }

    fun updateCard(side: FlashCard.Side, newText: String, card: Card) {
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
