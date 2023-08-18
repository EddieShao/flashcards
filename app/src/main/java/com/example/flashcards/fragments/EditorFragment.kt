package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.adapters.CardAdapter
import com.example.flashcards.viewmodels.EditorViewModel
import com.example.flashcards.databinding.FragmentEditorBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.views.SpaceDivider

class EditorFragment : Fragment() {
    private val viewModel by viewModels<EditorViewModel>()

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val adapter = CardAdapter(mutableListOf(), showFlip = true, showDelete = true)

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
            viewModel.loadData(stackId)
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

        binding.title.onFocusChangeListener = SystemHelper.hideKeypadListener
        binding.cardList.layoutManager = LinearLayoutManager(context)
        binding.cardList.addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))
        binding.cardList.adapter = adapter

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        viewModel.title.observe(viewLifecycleOwner) { title ->
            binding.title.setText(title)
        }
        viewModel.cards.observe(viewLifecycleOwner) { cards ->
            // boolean doesn't matter since we're not showing the face
            adapter.submitData(cards.map { card -> card to false })
        }
    }
}