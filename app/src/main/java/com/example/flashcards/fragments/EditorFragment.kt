package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.flashcards.viewmodels.EditorViewModel
import com.example.flashcards.databinding.FragmentEditorBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.helpers.SystemHelper
import kotlinx.coroutines.launch

class EditorFragment : Fragment() {
    private val viewModel by viewModels<EditorViewModel>()

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private var stackId: Int? = null // null => create new set, otherwise => update existing set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stackId = arguments?.let { args ->
            if (args.containsKey(NavArgs.STACK_ID.str)) {
                args.getInt(NavArgs.STACK_ID.str)
            } else {
                null
            }
        }
        stackId?.let { stackId ->
            viewModel.loadCards(stackId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hide keyboard when focus on edit text is lost
        binding.title.onFocusChangeListener =
            View.OnFocusChangeListener { editText, hasFocus ->
                lifecycleScope.launch {
                    if (!hasFocus) {
                        SystemHelper.hideKeypad(binding.title.windowToken)
                    }
                }
            }

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            // TODO: populate adapter
        }
    }
}