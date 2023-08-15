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
import com.example.flashcards.viewmodels.DropDownSettingValue
import com.example.flashcards.viewmodels.Setting
import com.example.flashcards.viewmodels.SettingValue

class SettingAdapter(
    private val settings: List<Setting>,
    private val onSettingChanged: (name: String, value: SettingValue) -> Unit
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
        private val onSettingChanged: (name: String, value: SettingValue) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = SettingsOptionBinding.bind(view)

        fun bind(setting: Setting) {
            binding.name.text = setting.name
            binding.description.text = setting.description
            when (setting.value) {
                is DropDownSettingValue -> {
                    binding.control.addView(inflateDropDownSetting(setting.name, setting.value))
                }
            }
        }

        private fun inflateDropDownSetting(name: String, setting: DropDownSettingValue): View {
            val binding = DropdownSettingControlBinding.inflate(
                LayoutInflater.from(binding.root.context), binding.root, false
            )
            binding.choice.text = setting.choice
            binding.menuButton.setOnClickListener { menuButton ->
                val popupMenu = PopupMenu(
                    ContextThemeWrapper(menuButton.context, R.style.MenuStyle), // force ltr
                    menuButton
                )
                for (option in setting.options) {
                    popupMenu.menu.add(option)
                }
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    binding.choice.text = menuItem.title
                    onSettingChanged(
                        name,
                        DropDownSettingValue(
                            choice = menuItem.title?.toString() ?: "",
                            setting.options
                        )
                    )
                    true
                }
                popupMenu.show()
            }
            return binding.root
        }
    }
}