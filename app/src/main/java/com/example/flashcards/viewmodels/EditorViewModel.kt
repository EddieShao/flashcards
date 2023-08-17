package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcards.data.entities.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditorViewModel : ViewModel() {
    private val toCreate = mutableListOf<Card>()
    private val toUpdate = mutableListOf<Card>()
    private val toDelete = mutableListOf<Card>()

    val cards = MutableLiveData<MutableList<Card>>(mutableListOf())

    fun loadCards(stackId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: replace with db call
            val data = mutableListOf(
                Card("Definition", "This is a description of the definition.", stackId, 0),
                Card("Lorem ipsum", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.", stackId, 1),
                Card("This is a really long definition. I'm not sure why you'd want to write a definition this long, but whatever suits your needs I guess.", "Short.", stackId, 2),
                Card("Another", "This is another description.", stackId, 3)
            )
            cards.postValue(data)
        }
    }
}