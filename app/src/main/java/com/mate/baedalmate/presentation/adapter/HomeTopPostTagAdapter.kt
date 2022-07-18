package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.databinding.ItemHomeTopPostTagBinding

class HomeTopPostTagAdapter :
    ListAdapter<String, HomeTopPostTagAdapter.HomeTopPostTagViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTopPostTagViewHolder =
        HomeTopPostTagViewHolder(
            ItemHomeTopPostTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: HomeTopPostTagAdapter.HomeTopPostTagViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeTopPostTagViewHolder(private val binding: ItemHomeTopPostTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tagText: String) {
            binding.tvHomeTopPostTag.text = tagText
        }
    }
}