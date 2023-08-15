package com.example.flashcards.views

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.helpers.NavArgs
import com.example.flashcards.R
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.databinding.StackBinding

class StackAdapter(
    private val stacks: MutableList<Stack>,
    private val onDeleteStack: (stack: Stack) -> Unit
) : RecyclerView.Adapter<StackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        StackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).root
    ) { stack ->
        onDeleteStack(stack)
    }

    override fun getItemCount() = stacks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stacks[position])
    }

    fun insert(stack: Stack) {
        // TODO
    }

    fun update(stack: Stack) {
        // TODO
    }

    fun delete(stack: Stack) {
        val pos = stacks.indexOf(stack)
        stacks.remove(stack)
        notifyItemRemoved(pos)
        notifyItemRangeChanged(pos, itemCount)
    }

    class ViewHolder(
        view: View,
        private val onDeleteStack: (stack: Stack) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = StackBinding.bind(view)

        fun bind(stack: Stack) {
            binding.title.run {
                text = stack.title
                viewTreeObserver.addOnGlobalLayoutListener(
                    object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewTreeObserver.removeOnGlobalLayoutListener(this)
                            maxLines = height / lineHeight
                            ellipsize = TextUtils.TruncateAt.END
                        }
                    }
                )
            }
            binding.playButton.setOnClickListener { playButton ->
                playButton.findNavController().navigate(
                    R.id.action_stackListFragment_to_practiceFragment,
                    bundleOf(
                        NavArgs.STACK_ID.str to stack.id
                    )
                )
            }
            binding.kebabMenu.setOnClickListener { kebabMenu ->
                val popupMenu = PopupMenu(kebabMenu.context, kebabMenu)
                popupMenu.menuInflater.inflate(R.menu.stack_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.edit -> {
                            kebabMenu.findNavController().navigate(
                                R.id.action_stackListFragment_to_editorFragment,
                                bundleOf(
                                    NavArgs.STACK_ID.str to stack.id
                                )
                            )
                        }
                        R.id.delete -> onDeleteStack(stack)
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }
}
