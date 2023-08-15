package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.databinding.FragmentSettingsBinding
import com.example.flashcards.viewmodels.SettingsViewModel
import com.example.flashcards.views.SettingAdapter
import com.example.flashcards.views.SpaceDivider

class SettingsFragment : Fragment() {
    private val viewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { _ ->
            findNavController().popBackStack()
        }

        binding.settingList.layoutManager = LinearLayoutManager(context)
        binding.settingList.addItemDecoration(SpaceDivider(sizeDp = 60, verticalPadding = false))

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            binding.settingList.adapter = SettingAdapter(settings) { name, value ->
                viewModel.updateSetting(name, value)
            }
        }
    }
}