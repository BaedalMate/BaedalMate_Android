package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.ItemHomeBottomPostRecentBinding

class HomeRecentPostAdapter :
    ListAdapter<RecruitDto, HomeRecentPostAdapter.HomeRecentPostViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RecruitDto>() {
            override fun areItemsTheSame(oldItem: RecruitDto, newItem: RecruitDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RecruitDto, newItem: RecruitDto): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecentPostViewHolder =
        HomeRecentPostViewHolder(
            ItemHomeBottomPostRecentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: HomeRecentPostAdapter.HomeRecentPostViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeRecentPostViewHolder(private val binding: ItemHomeBottomPostRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: RecruitDto) {
            with(binding) {
                tvHomeBottomPostRecentItemTopPerson.text = "${post.currentPeople}/${post.minPeople}"
                // TODO: 이 시간이 무엇을 의미하는것인지 알 수 있을까?
                tvHomeBottomPostRecentItemTopTime.text = "20분"

                tvHomeBottomPostRecentItemBottomTitle.text = "${post.title}"
                tvHomeBottomPostRecentItemBottomDeliveryCurrent.text = " ${post.deliveryFee}원"
                tvHomeBottomPostRecentItemBottomMinCurrent.text = " ${post.minPrice}원"
                tvHomeBottomPostRecentItemBottomUser.text = "${post.username} • ${post.dormitory}"
                tvHomeBottomPostRecentItemBottomUserStar.text = " ★ ${post.userScore}"
            }
        }
    }
}