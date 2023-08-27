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
import com.example.flashcards.viewmodels.EditorViewModel
import com.example.flashcards.databinding.FragmentEditorBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.views.Dialog
import com.example.flashcards.views.SnackBar

class EditorFragment : Fragment() {
    private val viewModel by viewModels<EditorViewModel> { EditorViewModel.Factory(stackId) }

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val warningSnackBar by lazy { SnackBar(binding.root, SnackBar.LENGTH_SHORT) }

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
            if (viewModel.isDirty) {
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
                viewModel.title = text.toString()
            }
        }

        binding.cardList.onDelete = { position ->
            showConfirmDeleteDialog(position)
        }
        binding.cardList.onTextChanged = { side, text, position ->
            viewModel.updateCard(position, side, text)
        }

        binding.back.setOnClickListener { button ->
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.newCard.setOnClickListener { button ->
            val card = viewModel.addCard(0)
            binding.cardList.add(0, card)
        }

        binding.save.setOnClickListener { button ->
            if (viewModel.cards.isEmpty()) {
                warningSnackBar.setText("You must add at least 1 card")
                warningSnackBar.show()
                SystemHelper.hideKeypad(view.windowToken)
            } else if (viewModel.title.isEmpty()) {
                warningSnackBar.setText("You must name your card set")
                warningSnackBar.show()
                SystemHelper.hideKeypad(view.windowToken)
            } else {
                viewModel.save()
                findNavController().popBackStack()
            }
        }

        viewModel.initStack.observe(viewLifecycleOwner) { stack ->
            binding.title.setText(stack.title)
        }

        viewModel.initCards.observe(viewLifecycleOwner) { cards ->
            binding.cardList.submitList(cards)
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

    private fun showConfirmDeleteDialog(position: Int) {
        Dialog(context).run {
            setTitle("Delete Card")
            setMessage("Are you sure you want to delete this card?")
            setPositiveButton("Delete") { dialog, which ->
                viewModel.removeCard(position)
                binding.cardList.remove(position)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            show()
        }
    }
}