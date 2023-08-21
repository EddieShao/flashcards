package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.adapters.CardAdapterState
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditorViewModel : ViewModel() {
    val initStack = MutableLiveData<Stack>()
    val initCards = MutableLiveData<List<Card>>()

    fun loadData(stackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Database.instance.stackDao().loadStackAndCards(stackId).entries.firstOrNull()?.let { (stack, cards) ->
                initStack.postValue(stack)
                initCards.postValue(cards)
            }
        }
    }

    fun createData(title: String, adapterState: CardAdapterState) {
        assert(adapterState.deletedCards.isEmpty())
        viewModelScope.launch(Dispatchers.IO) {
            val stackId = Database.instance.stackDao().insertStack(Stack(title))
            val toCreate = withContext(Dispatchers.Default) {
                adapterState.cards.map { card ->
                    // WARNING: We cast long to int which could cause long overflow. However,
                    //  realistically we won't ever reach that high of a number.
                    // TODO: consider if should change id values from int to long
                    Card(card.front, card.back, stackId.toInt())
                }
            }
            Database.instance.cardDao().insertCards(*toCreate.toTypedArray())
        }
    }

    fun updateData(stackId: Int, title: String, adapterState: CardAdapterState) {
        viewModelScope.launch(Dispatchers.Default) {
            val toCreate = adapterState.cards.filter { card -> card.data == null }.map { card ->
                Card(card.front, card.back, stackId)
            }
            val toUpdate = adapterState.cards.mapNotNull { card ->
                card.data?.let { data ->
                    Card(card.front, card.back, stackId, data.createdOn, data.id)
                }
            }
            val toDelete = adapterState.deletedCards.mapNotNull { card -> card.data }
            withContext(Dispatchers.IO) {
                initStack.value?.let { initStack ->
                    if (title != initStack.title) {
                        Database.instance.stackDao().updateStacks(
                            Stack(title, initStack.createdOn, initStack.id)
                        )
                    }
                }
                with(Database.instance.cardDao()) {
                    insertCards(*toCreate.toTypedArray())
                    updateCards(*toUpdate.toTypedArray())
                    deleteCards(*toDelete.toTypedArray())
                }
            }
        }
    }
}
