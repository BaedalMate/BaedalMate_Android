package com.mate.baedalmate.presentation.fragment.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.ItemPostCategoryListBinding

class PostCategoryListAdapter :
    ListAdapter<RecruitDto, PostCategoryListAdapter.PostCategoryListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<RecruitDto>() {
            override fun areItemsTheSame(
                oldItem: RecruitDto,
                newItem: RecruitDto,
            ) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RecruitDto,
                newItem: RecruitDto,
            ): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun analysisTypeInfoClick(data: RecruitDto, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PostCategoryListAdapter.PostCategoryListViewHolder =
        PostCategoryListViewHolder(
            ItemPostCategoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: PostCategoryListAdapter.PostCategoryListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostCategoryListViewHolder(private val binding: ItemPostCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecruitDto) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.layoutPostCategoryList.setOnClickListener {
                    listener?.analysisTypeInfoClick(item, pos)
                }
            }

//            binding.imgPostCategoryList = item.thumbnailImage // TODO: 서버와 연결하여 Glide 적용 필요
            binding.tvPostCategoryListTitle.text = item.title
            binding.tvPostCategoryListLocationStore.text = item.restaurantName
            binding.tvPostCategoryListLocationUser.text = item.dormitory
//            binding.tvPostCategoryListTimePassed.text = item.createDate // TODO: 작성된 시간 계산 필요
//            binding.tvPostCategoryListContentsStateGoalTitle.text =
//            binding.tvPostCategoryListContentsStateGoal.text =
            //TODO: 게시글 마감기준을 받아와 분기처리하여 보여주는 것이 필요

        }
    }
}