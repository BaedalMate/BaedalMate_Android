package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.databinding.ItemHomeBottomMenuBinding

class HomeCategoryAdapter :
    ListAdapter<String, HomeCategoryAdapter.HomeCategoryViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder =
        HomeCategoryViewHolder(
            ItemHomeBottomMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: HomeCategoryAdapter.HomeCategoryViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeCategoryViewHolder(private val binding: ItemHomeBottomMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            // TODO: 카테고리 이름 설정
            binding.tvHomeBottomMenuItem.text = "테스트"
            // TODO: 카테고리 이미지 설정
        }
    }
}