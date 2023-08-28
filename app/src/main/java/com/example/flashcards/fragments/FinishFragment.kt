package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.flashcards.R
import com.example.flashcards.databinding.FragmentFinishBinding
import com.example.flashcards.viewmodels.ProgressViewModel

class FinishFragment : Fragment() {
    private val viewModel by activityViewModels<ProgressViewModel>()

    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack(R.id.stackListFragment, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(onBackPressed)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed.isEnabled = true

        binding.numCorrect.text = "${viewModel.numCorrect} Correct"
        binding.numIncorrect.text = "${viewModel.numIncorrect} Incorrect"
        binding.review.setOnClickListener {
            findNavController().navigate(R.id.action_finishFragment_to_reviewFragment)
        }
        binding.retry.setOnClickListener {
            viewModel.retry()
            findNavController().popBackStack()
        }
        binding.home.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressed.isEnabled = false
        _binding = null
    }
}