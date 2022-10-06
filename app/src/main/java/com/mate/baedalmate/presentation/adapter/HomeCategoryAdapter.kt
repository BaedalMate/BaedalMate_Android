package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.R
import com.mate.baedalmate.databinding.ItemHomeBottomMenuBinding
import com.mate.baedalmate.presentation.fragment.home.HomeFragmentDirections

class HomeCategoryAdapter :
    ListAdapter<String, HomeCategoryAdapter.HomeCategoryViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
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
            binding.tvHomeBottomMenuItem.text = category
            when (category) {
//                "전체" -> { binding.imgHomeBottomMenuItem.setImageDrawable(ContextCompat.getDrawable(binding.imgHomeBottomMenuItem.context, R.drawable.img_category_asia))}
                "한식" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_korean
                        )
                    )
                }
                "중식" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_chinese
                        )
                    )
                }
                "일식" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_japanese
                        )
                    )
                }
                "양식" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_western
                        )
                    )
                }
                "패스트푸드" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_fastfood
                        )
                    )
                }
                "분식" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_bunsik
                        )
                    )
                }
                "카페·디저트" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_dessert
                        )
                    )
                }
                "치킨" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_chinese
                        )
                    )
                }
                "피자" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_pizza
                        )
                    )
                }
                "아시안" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_asia
                        )
                    )
                }
                "도시락" -> {
                    binding.imgHomeBottomMenuItem.setImageDrawable(
                        ContextCompat.getDrawable(
                            binding.imgHomeBottomMenuItem.context,
                            R.drawable.img_category_packedmeal
                        )
                    )
                }
            }
            binding.root.setOnClickListener {
                findNavController(binding.root.findFragment()).navigate(
                    HomeFragmentDirections.actionHomeFragmentToPostCategoryListFragment(
                        currentCategory = category
                    )
                )
            }
        }
    }
}