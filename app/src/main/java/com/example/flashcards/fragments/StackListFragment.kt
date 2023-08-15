package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.R
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.databinding.FragmentStackListBinding
import com.example.flashcards.viewmodels.StackListViewModel
import com.example.flashcards.viewmodels.sortBySetting
import com.example.flashcards.views.Dialog
import com.example.flashcards.views.SpaceDivider
import com.example.flashcards.views.StackAdapter
import kotlinx.coroutines.launch

class StackListFragment : Fragment() {
    private val viewModel by viewModels<StackListViewModel>()

    private var _binding: FragmentStackListBinding? = null
    private val binding get() = _binding!!

    private lateinit var stackAdapter: StackAdapter

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

        binding.settings.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_stackListFragment_to_settingsFragment)
        }

        binding.stackList.layoutManager = LinearLayoutManager(context)
        binding.stackList.addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))

        binding.fab.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_stackListFragment_to_editorFragment)
        }

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        viewModel.stacks.observe(viewLifecycleOwner) { stacks ->
            stacks.sortBySetting()
            stackAdapter = StackAdapter(stacks) { stack ->
                showConfirmDeleteDialog(stack)
            }
            binding.stackList.adapter = stackAdapter
        }
    }

    private fun showConfirmDeleteDialog(stack: Stack) {
        Dialog(context).run {
            setTitle("Delete Cards")
            setMessage("Are you sure you want to delete this set of cards?")
            setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteStack(stack)
                stackAdapter.delete(stack)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            show()
        }
    }
}