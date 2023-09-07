package com.example.flashcards.adapters

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcards.databinding.AttributionBinding

class AttributionAdapter : RecyclerView.Adapter<AttributionAdapter.ViewHolder>() {
    private var attrs = emptyList<Attribution>()

    inner class ViewHolder(val binding: AttributionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AttributionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AttributionAdapter.ViewHolder, position: Int) {
        val attr = attrs[position]
        holder.binding.title.text = "${attr.source} by ${attr.author} is licensed under ${attr.license}."
        attr.sourceLink?.let { link ->
            holder.binding.source.text = "Source: $link"
        } ?: run { holder.binding.source.visibility = View.GONE }
        attr.authorLink?.let { link ->
            holder.binding.author.text = "Author: $link"
        } ?: run { holder.binding.author.visibility = View.GONE }
        holder.binding.license.text = "License: ${attr.licenseLink}"
    }

    override fun getItemCount() = attrs.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newAttrs: List<Attribution>) {
        attrs = newAttrs
        notifyDataSetChanged()
    }
}

data class Attribution(
    val license: String,
    val source: String,
    val author: String,
    val licenseLink: String,
    val sourceLink: String? = null,
    val authorLink: String? = null
)
