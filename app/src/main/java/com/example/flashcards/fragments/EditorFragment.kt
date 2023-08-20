package com.example.flashcards.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.adapters.CardAdapter
import com.example.flashcards.adapters.DisplayCard
import com.example.flashcards.viewmodels.EditorViewModel
import com.example.flashcards.databinding.FragmentEditorBinding
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.helpers.SystemHelper
import com.example.flashcards.views.Dialog
import com.example.flashcards.views.SpaceDivider

class EditorFragment : Fragment() {
    private val viewModel by viewModels<EditorViewModel>()

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val adapter = CardAdapter(showFlip = true, showDelete = true)

    // If null, we're creating a new stack. If not null, we're editing an existing stack
    private var stackId: Int? = null

    private val dirty get() =
        viewModel.initTitle != binding.title.text.toString() || adapter.isDirty()

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

        binding.cardList.run {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(SpaceDivider(sizeDp = 48, verticalPadding = true))
            adapter = this@EditorFragment.adapter
        }

        binding.back.setOnClickListener { button ->
            if (dirty) {
                showConfirmLeaveDialog()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.newCard.setOnClickListener { button ->
            adapter.insertCard(position = 0, DisplayCard("", "", false))
        }

        binding.save.setOnClickListener { button ->
            if (dirty) {
                stackId?.let { stackId ->
                    viewModel.updateData(stackId, binding.title.text.toString(), adapter.state)
                } ?: run {
                    viewModel.createData(binding.title.text.toString(), adapter.state)
                }
            }
            findNavController().popBackStack()
        }

        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        viewModel.initData.observe(viewLifecycleOwner) { data ->
            binding.title.setText(data.stack.title)
            adapter.submitData(
                data.cards.map { card ->
                    DisplayCard(card.front, card.back, isHappy = false, card.id)
                }
            )
        }
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
}