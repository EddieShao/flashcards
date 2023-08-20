package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.adapters.CardAdapterState
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.data.entities.StackAndCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class EditorViewModel : ViewModel() {
    val initData = MutableLiveData<StackAndCards>()
    val initTitle get() = initData.value?.stack?.title.orEmpty()

    fun loadData(stackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = Database.instance.stackDao().loadStackAndCards(stackId)
            initData.postValue(data)
        }
    }

    fun createData(title: String, adapterState: CardAdapterState) {
        assert(adapterState.deletedCards.isEmpty())
        viewModelScope.launch(Dispatchers.IO) {
            val stackId = Database.instance.stackDao().insertStack(
                Stack(title, Calendar.getInstance().timeInMillis)
            )
            val toCreate = withContext(Dispatchers.Default) {
                adapterState.cards.map { card ->
                    // TODO: WARNING - cast to int might result in long overflow
                    Card(card.front, card.back, stackId.toInt())
                }
            }
            Database.instance.cardDao().insertCards(*toCreate.toTypedArray())
        }
    }

    fun updateData(stackId: Int, title: String, adapterState: CardAdapterState) {
        viewModelScope.launch(Dispatchers.Default) {
            val toCreate = adapterState.cards.filter { card -> card.id == null }.map { card ->
                Card(card.front, card.back, stackId)
            }
            val toUpdate = adapterState.cards.mapNotNull { card ->
                card.id?.let { id ->
                    Card(card.front, card.back, stackId, id)
                }
            }
            withContext(Dispatchers.IO) {
                if (title != initTitle) {
                    initData.value?.stack?.let { initStack ->
                        Database.instance.stackDao().updateStacks(
                            Stack(title, initStack.createdOn, initStack.id)
                        )
                    }
                }
                with(Database.instance.cardDao()) {
                    insertCards(*toCreate.toTypedArray())
                    updateCards(*toUpdate.toTypedArray())
                }
            }
        }
    }
}
