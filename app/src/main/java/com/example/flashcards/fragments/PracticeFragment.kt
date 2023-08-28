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
import com.example.flashcards.databinding.FragmentPracticeBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.viewmodels.Finished
import com.example.flashcards.viewmodels.InProgress
import com.example.flashcards.viewmodels.ProgressViewModel
import com.example.flashcards.views.Dialog

class PracticeFragment : Fragment() {
    private val viewModel by activityViewModels<ProgressViewModel>()

    private var _binding: FragmentPracticeBinding? = null
    private val binding get() = _binding!!

    private val stackId by lazy { requireArguments().getInt(NavArgs.STACK_ID.str) }

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currStat = viewModel.status.value
            if (currStat is InProgress && currStat.end > 0) {
                showConfirmLeaveDialog()
            } else {
                viewModel.finish()
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.start(stackId)
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

        binding.prev.setOnClickListener { viewModel.prev() }
        binding.next.setOnClickListener { viewModel.next() }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                is InProgress -> {
                    val react = { isHappy: Boolean ->
                        viewModel.next(isHappy)
                        if (status.curr == status.size - 1) {
                            findNavController().navigate(R.id.action_practiceFragment_to_finishFragment)
                        }
                    }

                    binding.prev.visibility = if (status.curr == 0) View.GONE else View.VISIBLE
                    binding.next.visibility = if (status.curr == status.end) View.GONE else View.VISIBLE
                    with(binding.card) {
                        front = status.card.front
                        back = status.card.back
                        // TODO: choose which side of card to reveal depending on setting
                    }
                    binding.progress.text = "${status.curr + 1} / ${status.size}"
                    binding.sad.setOnClickListener {
                        react(false)
                    }
                    binding.happy.setOnClickListener {
                        react(true)
                    }
                }
                Finished -> {}
            }
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
                viewModel.finish()
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