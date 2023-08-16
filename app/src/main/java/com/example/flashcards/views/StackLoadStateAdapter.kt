package com.example.flashcards.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.LoadStateBinding

class StackLoadStateAdapter : LoadStateAdapter<StackLoadStateAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: LoadStateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: StackLoadStateAdapter.ViewHolder, loadState: LoadState) {
        holder.binding.progress.isVisible = loadState is LoadState.Loading
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): StackLoadStateAdapter.ViewHolder {
        return ViewHolder(
            LoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}