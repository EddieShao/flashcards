package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

class ProgressViewModel : ViewModel() {
    private val cards = mutableListOf<ProgressCard>()

    val status = MutableLiveData<Status>()
    val finished = MutableLiveData<Unit>()

    fun start(stackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = Database.instance.cardDao().loadCardsWithStackId(stackId)
            cards.clear()
            cards.addAll(data.map { card -> ProgressCard(isCorrect = false, card) })
            status.postValue(Status(0, 0, data.first()))
        }
    }

    fun submit(index: Int, isCorrect: Boolean) {
        val nextIndex = index + 1
        cards[index].isCorrect = isCorrect

        if (nextIndex == cards.size) {
            finished.postValue(Unit)
        } else {
            status.postValue(
                Status(
                    current = nextIndex,
                    max(nextIndex, status.value?.end ?: 0),
                    cards[nextIndex].data
                )
            )
        }
    }

    fun statusAt(index: Int) {
        status.value?.let { stat ->
            assert(index <= stat.end)
            status.postValue(Status(index, stat.end, stat.card))
        }
    }
}

data class ProgressCard(var isCorrect: Boolean, val data: Card)

data class Status(
    val current: Int, // index (in cards list) of card currently on screen
    val end: Int, // index (in cards list) of the latest card visited
    val card: Card
)
