package com.example.flashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.LoadStateBinding

class StackLoadStateAdapter : LoadStateAdapter<StackLoadStateAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: LoadStateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.binding.progress.isVisible = loadState is LoadState.Loading
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ViewHolder {
        return ViewHolder(
            LoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
}