package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.models.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

class ProgressViewModel : ViewModel() {
    var cards = emptyList<CardModel>()
    val status = MutableLiveData<Status>(Finished)

    val numCorrect get() = cards.count { card -> card.isHappy }
    val numIncorrect get() = cards.count { card -> !card.isHappy }

    fun start(stackId: Int) {
        doIfStatus<Finished> {
            viewModelScope.launch(Dispatchers.IO) {
                cards = Database.instance.cardDao().loadCardsWithStackId(stackId).map { card ->
                    CardModel(card.front, card.back, false, card.createdOn, card.id)
                }
                status.postValue(InProgress(0, 0, cards.size, cards.first()))
            }
        }
    }

    fun retry() {
        doIfStatus<Finished> {
            cards = cards.shuffled()
            // We MUST call this from the main (aka blocking) thread because its value must be
            // changed to InProgress before we possibly call start again.
            status.value = InProgress(0, 0, cards.size, cards.first())
        }
    }

    fun next(isHappy: Boolean? = null) {
        doIfStatus<InProgress> { progress ->
            isHappy?.let { cards[progress.curr].isHappy = it }
            val next = progress.curr + 1
            if (next == progress.size) {
                status.value = Finished
            } else {
                status.value = InProgress(next, max(next, progress.end), progress.size, cards[next])
            }
        }
    }

    fun prev() {
        doIfStatus<InProgress> { progress ->
            val prev = progress.curr - 1
            status.value = InProgress(prev, progress.end, progress.size, cards[prev])
        }
    }

    fun finish() {
        doIfStatus<InProgress> {
            status.value = Finished
        }
    }

    private inline fun <reified T : Status> doIfStatus(block: (T) -> Unit) {
        status.value?.let { status ->
            if (status is T) {
                block(status)
            }
        }
    }
}

sealed class Status
object Finished : Status()
data class InProgress(val curr: Int, val end: Int, val size: Int, val card: CardModel) : Status()
