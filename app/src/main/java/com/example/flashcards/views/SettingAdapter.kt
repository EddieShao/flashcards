package com.example.flashcards.views

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.databinding.DropdownSettingControlBinding
import com.example.flashcards.databinding.SettingsOptionBinding
import com.example.flashcards.helpers.Setting
import com.example.flashcards.helpers.SettingsHelper

class SettingAdapter(
    private val settings: List<Setting>,
    private val onSettingChanged: (setting: Setting, newValue: Any) -> Unit
) :
    RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        SettingsOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root,
        onSettingChanged
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(settings[position])
    }

    override fun getItemCount() = settings.size

    class ViewHolder(
        view: View,
        private val onSettingChanged: (setting: Setting, newValue: Any) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = SettingsOptionBinding.bind(view)

        fun bind(setting: Setting) {
            binding.name.text = setting.key
            binding.description.text = setting.description
            when (setting.type) {
                String::class -> {
                    binding.control.addView(inflateDropDownSetting(setting))
                }
            }
        }

        private fun inflateDropDownSetting(setting: Setting): View {
            val binding = DropdownSettingControlBinding.inflate(
                LayoutInflater.from(binding.root.context), binding.root, false
            )
            binding.choice.text = SettingsHelper.currentValueOf(setting) as String
            binding.menuButton.setOnClickListener { menuButton ->
                val popupMenu = PopupMenu(
                    ContextThemeWrapper(menuButton.context, R.style.MenuStyle), // force ltr
                    menuButton
                )
                for (option in setting.options) {
                    popupMenu.menu.add(option as String)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    binding.choice.text = menuItem.title
                    onSettingChanged(setting, menuItem.title ?: setting.default)
                    true
                }
                popupMenu.show()
            }
            return binding.root
        }
    }
}