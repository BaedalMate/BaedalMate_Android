package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitDto
import com.mate.baedalmate.databinding.ItemHomeBottomPostRecommendBinding

class HomeRecommendPostAdapter :
    ListAdapter<RecruitDto, HomeRecommendPostAdapter.HomeRecommendPostViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RecruitDto>() {
            override fun areItemsTheSame(oldItem: RecruitDto, newItem: RecruitDto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RecruitDto, newItem: RecruitDto): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecommendPostViewHolder =
        HomeRecommendPostViewHolder(
            ItemHomeBottomPostRecommendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: HomeRecommendPostAdapter.HomeRecommendPostViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeRecommendPostViewHolder(private val binding: ItemHomeBottomPostRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: RecruitDto) {
            with(binding) {
                tvHomeBottomPostRecommendItemInfoTitle.text = "${post.title}"
                tvHomeBottomPostRecentItemTopPerson.text = "${post.currentPeople}/${post.minPeople}"
                tvHomeBottomPostRecommendItemInfoBottomDeliveryCurrent.text =
                    " ${post.deliveryFee}원"
                tvHomeBottomPostRecommendItemInfoBottomCurrent.text = " ${post.minPrice}원"
                tvHomeBottomPostRecommendItemInfoBottomUser.text =
                    "${post.username} . ${post.dormitory} ★ ️${post.userScore}"
            }
            // TODO: 시간 설정
            // TODO: 카테고리 이미지 설정
        }
    }
}