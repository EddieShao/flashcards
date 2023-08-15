package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flashcards.helpers.Setting
import com.example.flashcards.helpers.SettingsHelper

class SettingsViewModel : ViewModel() {
    val settings = MutableLiveData<List<Setting>>(emptyList())

    init {
        settings.postValue(SettingsHelper.settings())
    }

    fun updateSetting(setting: Setting, newValue: Any) {
        SettingsHelper.updateValueOf(setting, newValue)
    }
}
