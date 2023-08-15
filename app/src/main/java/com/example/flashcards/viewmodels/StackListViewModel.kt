package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.entities.Stack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StackListViewModel : ViewModel() {
    val stacks = MutableLiveData<MutableList<Stack>>(mutableListOf())

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
}
