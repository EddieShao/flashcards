package com.example.flashcards.helpers

import com.example.flashcards.data.Database
import com.example.flashcards.models.CardModel
import com.example.flashcards.views.FlashCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

// TODO: replace all exceptions with logs (except for init block and invalid index cases)
class ProgressManager private constructor(val cards: List<CardModel>) {
    private val initStatus = InProgress(0, -1, 0, cards.size, cards[0])

    private val _status = MutableStateFlow<Status>(initStatus)
    val status = _status.asStateFlow()

    init {
        assert(cards.isNotEmpty()) { "ProgressManager initialized with empty list of cards." }
    }

    fun setVisibleSide(index: Int, side: FlashCard.Side) {
        cards[index].visibleSide = side
    }

    suspend fun retry() {
        for (card in cards) {
            card.visibleSide = decideRevealSide()
            card.isHappy = false
        }
        _status.emit(initStatus)
    }

    suspend fun next(isHappy: Boolean? = null) {
        (_status.value as? InProgress)?.let { progress ->
            isHappy?.let {
                cards[progress.currIndex].isHappy = it
            }

            val nextIndex = progress.currIndex + 1

            if (nextIndex > progress.endIndex && isHappy == null) {
                throw Exception("Advanced to new card without setting isHappy of current card.")
            } else if (nextIndex >= cards.size) {
                _status.emit(Finished)
            } else { // nextIndex < cards.size && (nextIndex <= progress.endIndex || isHappy != null)
                _status.emit(
                    InProgress(
                        currIndex = nextIndex,
                        prevIndex = progress.currIndex,
                        endIndex = max(nextIndex, progress.endIndex),
                        size = cards.size,
                        card = cards[nextIndex]
                    )
                )
            }
        } ?: throw Exception("Instance is not in progress.")
    }

    suspend fun prev() {
        (_status.value as? InProgress)?.let { progress ->
            val prevIndex = progress.currIndex - 1

            if (prevIndex < 0) {
                throw Exception("Current card is already the first card in the list.")
            }

            _status.emit(
                InProgress(
                    currIndex = prevIndex,
                    prevIndex = progress.currIndex,
                    endIndex = progress.endIndex,
                    size = cards.size,
                    card = cards[prevIndex]
                )
            )
        } ?: throw Exception("Instance is not in progress.")
    }

    companion object {
        private val ioScope = CoroutineScope(Dispatchers.IO)

        private var _instance: ProgressManager? = null
        val instance get() = _instance ?: throw Exception("Instance is not initialized.")

        fun start(stackId: Int) {
            ioScope.launch {
                val manager = ProgressManager(
                    Database.instance.cardDao().loadCardsWithStackId(stackId).map { card ->
                        CardModel(
                            front = card.front,
                            back = card.back,
                            isHappy = false,
                            visibleSide = decideRevealSide(),
                            createdOn = card.createdOn,
                            id = card.id
                        )
                    }
                )
                synchronized(this) {
                    _instance = manager
                }
            }
        }

        fun stop() {
            synchronized(this) {
                _instance = null
            }
        }

        private fun decideRevealSide() =
            when (SettingsHelper.currentOptionOf<RevealSideOption>(Setting.REVEAL_SIDE)) {
                RevealSideOption.FRONT -> FlashCard.Side.FRONT
                RevealSideOption.BACK -> FlashCard.Side.BACK
                RevealSideOption.RANDOM ->
                    if (Random.nextBoolean()) FlashCard.Side.FRONT else FlashCard.Side.BACK
                else -> FlashCard.Side.FRONT
            }
    }
}

sealed class Status
object Finished : Status()
data class InProgress(
    val currIndex: Int,
    val prevIndex: Int,
    val endIndex: Int,
    val size: Int,
    val card: CardModel
) : Status()
