package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.flashcards.databinding.FragmentPracticeBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.viewmodels.ProgressViewModel

class PracticeFragment : Fragment() {
    private val viewModel by activityViewModels<ProgressViewModel>()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val stackId by lazy { requireArguments().getInt(NavArgs.STACK_ID.str) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData(stackId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}