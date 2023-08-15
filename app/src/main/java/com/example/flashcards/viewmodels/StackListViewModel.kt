package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.helpers.Setting
import com.example.flashcards.helpers.SettingsHelper
import com.example.flashcards.helpers.StackSortOrderOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StackListViewModel : ViewModel() {
    val stacks = MutableLiveData<MutableList<Stack>>(mutableListOf())

    val confirmSearchText = MutableLiveData<Unit>()
    private var searchTextJob: Job? = null

    init {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                // TODO: replace with db call
                mutableListOf(
                    Stack("This is a Title", 200, 0),
                    Stack("Short", 100, 1),
                    Stack("This is a very long title. The entire title may not fit inside the allocated space, so the user will see some trailing ellipses to indicate that the title continues.", 374, 2),
                    Stack("Another Title to Test List Scrolling", 90, 3),
                    Stack("Another", 800, 4)
                )
            }
            stacks.postValue(data)
        }
    }

    fun deleteStack(stack: Stack) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: db call to delete
        }
        stacks.value?.remove(stack)
    }

    // Start a count down to confirm the user's updated search bar text. Each time the user changes
    //  the search bar text BEFORE this count down finishes, restart the count down. Once the count
    //  down finishes, confirm the change and update the UI.
    //  -----
    // We do this to reduce the amount of times the stack list needs to update. If the user types
    // 5 characters in quick succession, we only want to show update results once, not 5 times.
    fun notifySearchTextChanged() {
        searchTextJob?.let { job ->
            if (!job.isCancelled) job.cancel()
        }
        searchTextJob = viewModelScope.launch(Dispatchers.Default) {
            delay(1000) // TODO: change this to desired wait time
            if (isActive) {
                confirmSearchText.postValue(Unit)
            }
        }
    }
}

// TODO: move this into coroutine on Dispatchers.Default
fun MutableList<Stack>.sortBySetting() {
    when (SettingsHelper.currentOptionOf<StackSortOrderOption>(Setting.STACK_SORT_ORDER)) {
        StackSortOrderOption.RECENTLY_ADDED -> sortBy { it.createdOn }
        StackSortOrderOption.TITLE -> sortBy { it.title }
        else -> {}
    }
}
