package com.example.flashcards.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.helpers.PreferenceHelper

class SettingsViewModel : ViewModel() {
    val settings = MutableLiveData<List<Setting>>(emptyList())

    init {
        settings.postValue(
            listOf(
                revealSide(),
                stackSortOrder()
            )
        )
    }

    fun updateSetting(name: String, value: SettingValue) {
        when (value) {
            is DropDownSettingValue -> PreferenceHelper.putString(name, value.choice)
        }
    }
}

data class Setting(val name: String, val description: String, val value: SettingValue)

sealed class SettingValue
class DropDownSettingValue(val choice: String, val options: List<String>) : SettingValue()

enum class SettingKey(val key: String, val defaultValue: Any) {
    REVEAL_SIDE("Reveal Side", "Front"),
    SORT_CARD_SETS_BY("Sort Card Sets By", "Recently Added")
}

private fun revealSide() = Setting(
    name = SettingKey.REVEAL_SIDE.key,
    description = "The side of each card that will be shown to you first.",
    value = DropDownSettingValue(
        choice = PreferenceHelper.getString(
            key = SettingKey.REVEAL_SIDE.key,
            default = SettingKey.REVEAL_SIDE.defaultValue as String
        ),
        options = listOf("Front", "Back", "Random")
    )
)

enum class StackSortOrderOption(val str: String) {
    TITLE("Title"),
    RECENTLY_ADDED("Recently Added");

    companion object {
        fun fromString(str: String) = StackSortOrderOption.values().find { option ->
            option.str == str
        }
    }
}

private fun stackSortOrder() = Setting(
    name = SettingKey.SORT_CARD_SETS_BY.key,
    description = "The order your card sets will appear in on the home page.",
    value = DropDownSettingValue(
        choice = PreferenceHelper.getString(
            key = SettingKey.SORT_CARD_SETS_BY.key,
            default = SettingKey.SORT_CARD_SETS_BY.defaultValue as String
        ),
        options = StackSortOrderOption.values().map { option -> option.str }
    )
)

fun MutableList<Stack>.sortBySetting() {
    StackSortOrderOption.fromString(
        PreferenceHelper.getString(
            SettingKey.SORT_CARD_SETS_BY.key,
            SettingKey.SORT_CARD_SETS_BY.defaultValue as String
        )
    )?.let { option ->
        when (option) {
            StackSortOrderOption.TITLE -> sortBy { stack -> stack.title }
            StackSortOrderOption.RECENTLY_ADDED -> sortBy { stack -> stack.createdOn }
        }
    }
}
