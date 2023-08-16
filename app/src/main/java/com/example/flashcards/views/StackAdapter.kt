package com.example.flashcards.views

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.R
import com.example.flashcards.data.entities.Stack
import com.example.flashcards.databinding.StackBinding
import com.example.flashcards.helpers.NavArgs

class StackAdapter : PagingDataAdapter<Stack, StackAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stack>() {
            override fun areItemsTheSame(oldItem: Stack, newItem: Stack): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Stack, newItem: Stack): Boolean =
                oldItem == newItem
        }
    }

    inner class ViewHolder(val binding: StackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { stack ->
            holder.binding.title.run {
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
            holder.binding.playButton.setOnClickListener { playButton ->
                playButton.findNavController().navigate(
                    R.id.action_stackListFragment_to_practiceFragment,
                    bundleOf(
                        NavArgs.STACK_ID.str to stack.id
                    )
                )
            }
            holder.binding.kebabMenu.setOnClickListener { kebabMenu ->
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
                        R.id.delete -> {
                            // TODO: callback delete item
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            StackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}
