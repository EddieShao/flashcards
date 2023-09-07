package com.example.flashcards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.adapters.Attribution
import com.example.flashcards.adapters.AttributionAdapter
import com.example.flashcards.databinding.FragmentAttributionsBinding
import com.example.flashcards.views.SpaceDivider

class AttributionsFragment : Fragment() {
    private lateinit var binding: FragmentAttributionsBinding
    private val adapter = AttributionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttributionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.attributions.layoutManager = LinearLayoutManager(context)
        binding.attributions.addItemDecoration(SpaceDivider(sizeDp = 60, verticalPadding = false))
        binding.attributions.adapter = adapter.also { it.submitList(ATTRS) }
    }

    companion object {
        val ATTRS = listOf(
            Attribution(
                license = "CC BY 3.0",
                source = "LushLife_LevelUp.wav",
                author = "SimonBay",
                licenseLink = "https://creativecommons.org/licenses/by/3.0/",
                sourceLink = "https://freesound.org/people/SimonBay/sounds/439889/",
                authorLink = "https://freesound.org/people/SimonBay/"
            ),
            Attribution(
                license = "CC BY 4.0",
                source = "Card Flip",
                author = "f4ngy",
                licenseLink = "https://creativecommons.org/licenses/by/4.0/",
                sourceLink = "https://freesound.org/people/f4ngy/sounds/240776/",
                authorLink = "https://freesound.org/people/f4ngy/"
            )
        )
    }
}
