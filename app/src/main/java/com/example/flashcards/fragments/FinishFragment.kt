package com.example.flashcards.fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.flashcards.R
import com.example.flashcards.databinding.FragmentFinishBinding
import com.example.flashcards.helpers.ProgressManager
import com.example.flashcards.viewmodels.FinishViewModel

class FinishFragment : Fragment() {
    private val viewModel by viewModels<FinishViewModel>()

    private var _binding: FragmentFinishBinding? = null
    private val binding get() = _binding!!

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            ProgressManager.stop()
            findNavController().popBackStack(R.id.stackListFragment, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: figure out how to only play this once. right now it plays whenever screen changes
        //  vertical/horizontal (whenever onCreate is called)
        MediaPlayer.create(context, R.raw.finish).start()
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

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressed)

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
        _binding = null
    }
}