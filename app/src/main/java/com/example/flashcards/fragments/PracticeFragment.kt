package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.flashcards.databinding.FragmentPracticeBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.viewmodels.ProgressViewModel
import com.example.flashcards.views.Dialog

class PracticeFragment : Fragment() {
    private val viewModel by activityViewModels<ProgressViewModel>()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val stackId by lazy { requireArguments().getInt(NavArgs.STACK_ID.str) }

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showConfirmLeaveDialog()
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
        _binding = FragmentPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed.isEnabled = true

        binding.back.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressed.isEnabled = false
        _binding = null
    }

    private fun showConfirmLeaveDialog() {
        Dialog(context).run {
            setTitle("Leave Practice")
            setMessage("Are you sure you want to leave? Your progress will be lost.")
            setPositiveButton("Leave") { dialog, which ->
                findNavController().popBackStack()
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}