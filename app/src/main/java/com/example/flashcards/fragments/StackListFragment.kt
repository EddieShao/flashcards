package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.R
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.databinding.FragmentStackListBinding
import com.example.flashcards.viewmodels.StackListViewModel
import com.example.flashcards.views.Dialog
import com.example.flashcards.views.SpaceDivider
import com.example.flashcards.views.StackAdapter
import com.example.flashcards.views.StackLoadStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StackListFragment : Fragment() {
    private val viewModel by viewModels<StackListViewModel>()

    private var _binding: FragmentStackListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStackListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hide keyboard when focus on search bar is lost
        binding.editText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                lifecycleScope.launch {
                    if (!hasFocus) {
                        SystemHelper.hideKeypad(binding.editText.windowToken)
                    }
                }
            }

        binding.editText.doOnTextChanged { text, start, before, count ->
            viewModel.updateQuery(text.toString())
        }

        binding.settings.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_stackListFragment_to_settingsFragment)
        }

        val adapter = StackAdapter()
        binding.stackList.layoutManager = LinearLayoutManager(context)
        binding.stackList.addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))
        binding.stackList.adapter = adapter.withLoadStateFooter(StackLoadStateAdapter())
        lifecycleScope.launch {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.fab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_stackListFragment_to_editorFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmDeleteDialog(stack: Stack) {
        Dialog(context).run {
            setTitle("Delete Cards")
            setMessage("Are you sure you want to delete this set of cards?")
            setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteStack(stack)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            show()
        }
    }
}