package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.adapters.CardAdapter
import com.example.flashcards.adapters.ProgressCard
import com.example.flashcards.viewmodels.EditorViewModel
import com.example.flashcards.databinding.FragmentEditorBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.views.Dialog
import com.example.flashcards.views.SnackBar
import com.example.flashcards.views.SpaceDivider

class EditorFragment : Fragment() {
    private val viewModel by viewModels<EditorViewModel> { EditorViewModel.Factory(stackId) }

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val adapter = CardAdapter(
        showFlip = true,
        showDelete = true,
        onDelete = { card -> showConfirmDeleteDialog(card) },
        onTextChanged = { side, newText, card -> viewModel.updateCard(side, newText, card.data) }
    )

    private val warningSnackBar by lazy {
        SnackBar(binding.root, SnackBar.LENGTH_SHORT).apply {
            setText("You must add at least 1 card")
        }
    }

    // If null, we're creating a new stack. If not null, we're editing an existing stack
    private val stackId by lazy {
        arguments?.let { args ->
            if (args.containsKey(NavArgs.STACK_ID.str)) {
                args.getInt(NavArgs.STACK_ID.str)
            } else {
                null
            }
        }
    }

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.dirty) {
                showConfirmLeaveDialog()
            } else {
                findNavController().popBackStack()
            }
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
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed.isEnabled = true

        with(binding.title) {
            onFocusChangeListener = SystemHelper.hideKeypadListener
            doOnTextChanged { text, start, before, count ->
                viewModel.updateTitle(text.toString())
            }
        }

        binding.cardList.run {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))
            adapter = this@EditorFragment.adapter
        }

        binding.back.setOnClickListener { button ->
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.newCard.setOnClickListener { button ->
            adapter.addCard(ProgressCard(viewModel.createCard(), isHappy = false), 0)
        }

        binding.save.setOnClickListener { button ->
            if (adapter.currentList.isEmpty()) {
                warningSnackBar.show()
            } else {
                viewModel.save()
                findNavController().popBackStack()
            }
        }

        viewModel.initStack.observe(viewLifecycleOwner) { stack ->
            binding.title.setText(stack.title)
        }

        viewModel.initCards.observe(viewLifecycleOwner) { cards ->
            // isHappy doesn't matter since we're not using it here
            adapter.submitList(cards.map { card -> ProgressCard(card, isHappy = false) })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressed.isEnabled = false
        _binding = null
    }

    private fun showConfirmLeaveDialog() {
        Dialog(context).run {
            setTitle("Leave Editor")
            setMessage("Are you sure you want to leave? Your changes will be lost.")
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

    private fun showConfirmDeleteDialog(card: ProgressCard) {
        Dialog(context).run {
            setTitle("Delete Card")
            setMessage("Are you sure you want to delete this card?")
            setPositiveButton("Delete") { dialog, which ->
                viewModel.deleteCard(card.data)
                adapter.removeCard(card)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}