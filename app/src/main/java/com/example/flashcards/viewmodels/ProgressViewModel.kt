package com.example.flashcards.viewmodels

import androidx.lifecycle.ViewModel

class ProgressViewModel : ViewModel() {
    fun loadData(stackId: Int) {
        // TODO: load card data from db
    }

    fun clear() {
        // TODO: clear all data so garbage collection can clean up memory
    }
}