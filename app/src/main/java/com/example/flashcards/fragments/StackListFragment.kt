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
import com.example.flashcards.adapters.StackAdapter
import com.example.flashcards.adapters.StackLoadStateAdapter
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

        binding.searchIcon.setOnClickListener { searchIcon ->
            binding.editText.requestFocus()
            SystemHelper.showKeypad(binding.editText)
        }

        binding.editText.onFocusChangeListener = SystemHelper.hideKeypadListener
        binding.editText.doOnTextChanged { text, start, before, count ->
            viewModel.updateQuery(text.toString())
            binding.clearText.visibility =
                if (text.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
        }

        binding.clearText.visibility =
            if (binding.editText.text.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
        binding.clearText.setOnClickListener {
            binding.editText.setText("")
        }

        binding.settings.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_stackListFragment_to_settingsFragment)
        }

        binding.stackList.layoutManager = LinearLayoutManager(context)
        binding.stackList.addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))
        val adapter = StackAdapter { stack ->
            showConfirmDeleteDialog(stack)
        }
        adapter.addLoadStateListener { states ->
            if (states.append.endOfPaginationReached) {
                if (adapter.itemCount < 1) {
                    binding.beginnerNote.text =
                        if (binding.editText.text.isNullOrEmpty()) {
                            "Create your first set"
                        } else {
                            "Nothing to see here..."
                        }
                    binding.beginnerNote.visibility = View.VISIBLE
                } else {
                    binding.beginnerNote.visibility = View.INVISIBLE
                }
            }
        }
        binding.stackList.adapter = adapter.withLoadStateFooter(StackLoadStateAdapter())
        lifecycleScope.launch {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.fab.setOnClickListener { fab ->
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
            setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteStack(stack)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}