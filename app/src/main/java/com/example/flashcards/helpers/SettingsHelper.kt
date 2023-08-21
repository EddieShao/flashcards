package com.example.flashcards.helpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KClass

object SettingsHelper {
    private var prefs: SharedPreferences? = null

    private val settings = mutableMapOf<Setting, Any>()

    fun init(activity: Activity) {
        prefs = activity.getPreferences(Context.MODE_PRIVATE)
        settings.clear()
        settings.putAll(
            Setting.values().mapNotNull { setting ->
                when (setting.type) {
                    String::class -> {
                        setting to (prefs?.getString(setting.key, setting.default as String) ?: setting.default)
                    }
                    else -> {
                        null
                    }
                }
            }
        )
    }

    fun destroy() {
        prefs = null
    }

    fun settings() = settings.toList().map { it.first }

    fun currentValueOf(setting: Setting) = settings[setting] ?: setting.default

    inline fun <reified E : Enum<*>> currentOptionOf(setting: Setting): E? {
        val curr = currentValueOf(setting)
        return when (E::class) {
            StackSortOrderOption::class -> {
                StackSortOrderOption.values().find { it.str == curr as String } as E
            }
            RevealSideOption::class -> {
                RevealSideOption.values().find { it.str == curr as String } as E
            }
            else -> null
        }
    }

    fun updateValueOf(setting: Setting, value: Any) {
        when (setting.type) {
            String::class -> {
                prefs?.edit()?.run {
                    putString(setting.key, value as String)
                    apply()
                }
            }
        }
        settings[setting] = value
    }
}

enum class Setting(
    val key: String,
    val title: String,
    val description: String,
    val type: KClass<*>,
    val options: List<Any>,
    val default: Any
) {
    STACK_SORT_ORDER(
        key = "stack-sort-order",
        title = "Sort Card Sets By",
        description = "The order your card sets appear in on the home page.",
        type = String::class,
        options = listOf(
            StackSortOrderOption.RECENTLY_ADDED.str,
            StackSortOrderOption.TITLE.str
        ),
        default = StackSortOrderOption.RECENTLY_ADDED.str
    ),
    REVEAL_SIDE(
        key = "reveal-side",
        title = "Reveal Side",
        description = "The side of each card that will be shown to you first.",
        type = String::class,
        options = listOf(
            RevealSideOption.FRONT.str,
            RevealSideOption.BACK.str,
            RevealSideOption.RANDOM.str
        ),
        default = RevealSideOption.FRONT.str
    )
}

enum class StackSortOrderOption(val str: String) {
    RECENTLY_ADDED("Recently Added"),
    TITLE("Title")
}

enum class RevealSideOption(val str: String) {
    FRONT("Front"),
    BACK("Back"),
    RANDOM("Random")
}
