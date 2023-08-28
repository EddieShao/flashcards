package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.Database
import com.example.flashcards.helpers.RevealSideOption
import com.example.flashcards.helpers.Setting
import com.example.flashcards.helpers.SettingsHelper
import com.example.flashcards.models.CardModel
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

class ProgressViewModel : ViewModel() {
    var cards = emptyList<CardModel>()
    val status = MutableLiveData<Status>(Finished)

    val numCorrect get() = cards.count { card -> card.isHappy }
    val numIncorrect get() = cards.count { card -> !card.isHappy }

    fun start(stackId: Int) {
        doIfStatus<Finished> {
            viewModelScope.launch(Dispatchers.IO) {
                cards = Database.instance.cardDao().loadCardsWithStackId(stackId).map { card ->
                    CardModel(
                        front = card.front,
                        back = card.back,
                        isHappy = false,
                        visibleSide = decideRevealSide(),
                        createdOn = card.createdOn,
                        id = card.id
                    )
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

    fun setVisibleSide(index: Int, side: FlashCard.Side) {
        cards[index].visibleSide = side
    }

    private fun decideRevealSide() =
        when (SettingsHelper.currentOptionOf<RevealSideOption>(Setting.REVEAL_SIDE)) {
            RevealSideOption.FRONT -> FlashCard.Side.FRONT
            RevealSideOption.BACK -> FlashCard.Side.BACK
            RevealSideOption.RANDOM ->
                if (Random.nextBoolean()) FlashCard.Side.FRONT else FlashCard.Side.BACK
            else -> FlashCard.Side.FRONT
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
