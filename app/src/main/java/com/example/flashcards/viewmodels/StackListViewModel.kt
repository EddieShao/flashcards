package com.example.flashcards.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.flashcards.data.Database
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.data.paging.StackPagingSource
import com.example.flashcards.helpers.Setting
import com.example.flashcards.helpers.SettingsHelper
import com.example.flashcards.helpers.StackSortOrderOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StackListViewModel : ViewModel() {
    private var searchQueryJob: Job? = null
    private var searchQuery: String = ""

    private var stackPagingSource: StackPagingSource? = null
    val data = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
    ) {
        StackPagingSource(query = { searchQuery }, order = { sortOrder() }).also {
            stackPagingSource = it
        }
    }.flow.cachedIn(viewModelScope)

    fun deleteStack(stack: Stack) {
        viewModelScope.launch(Dispatchers.IO) {
            Database.instance.stackDao().deleteStacks(stack)
            stackPagingSource?.invalidate()
        }
    }

    // Start a count down to confirm the user's updated search bar text. Each time the user changes
    //  the search bar text BEFORE this count down finishes, restart the count down. Once the count
    //  down finishes, confirm the change and update the UI.
    //  -----
    // We do this to reduce the amount of times the stack list needs to update. If the user types
    //  5 characters in quick succession, we only want to show update results once, not 5 times.
    fun updateQuery(query: String) {
        searchQueryJob?.let { job ->
            if (!job.isCancelled) job.cancel()
        }
        searchQueryJob = viewModelScope.launch(Dispatchers.Default) {
            delay(timeMillis = 500)
            if (isActive) {
                searchQuery = query
                stackPagingSource?.invalidate()
            }
        }
    }

    private fun sortOrder() = when (
        SettingsHelper.currentOptionOf<StackSortOrderOption>(Setting.STACK_SORT_ORDER)
    ) {
        StackSortOrderOption.TITLE -> "title"
        StackSortOrderOption.RECENTLY_ADDED -> "created_on"
        else -> "created_on"
    }
}
