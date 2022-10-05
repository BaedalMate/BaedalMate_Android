package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.databinding.ItemHomeTopPostTagBinding
import com.mate.baedalmate.domain.model.TagDto

class HomeTopPostTagAdapter :
    ListAdapter<TagDto, HomeTopPostTagAdapter.HomeTopPostTagViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<TagDto>() {
            override fun areItemsTheSame(oldItem: TagDto, newItem: TagDto) = oldItem == newItem
            override fun areContentsTheSame(oldItem: TagDto, newItem: TagDto): Boolean =
                oldItem.tagname == newItem.tagname
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

    override fun submitList(list: MutableList<TagDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeTopPostTagViewHolder(private val binding: ItemHomeTopPostTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: TagDto) {
            binding.tvHomeTopPostTag.text = tag.tagname
        }
    }
}