package com.mate.baedalmate.presentation.adapter.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitResponseDto
import com.mate.baedalmate.databinding.ItemHistoryPostBinding
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryPostAdapter(private val requestManager: RequestManager) :
    ListAdapter<HistoryRecruitResponseDto, HistoryPostAdapter.HistoryPostViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<HistoryRecruitResponseDto>() {
            override fun areItemsTheSame(
                oldItem: HistoryRecruitResponseDto,
                newItem: HistoryRecruitResponseDto,
            ) =
                oldItem.recruitId == newItem.recruitId

            override fun areContentsTheSame(
                oldItem: HistoryRecruitResponseDto,
                newItem: HistoryRecruitResponseDto,
            ): Boolean =
                oldItem == newItem
        }
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    interface OnItemClickListener {
        fun postClick(postId: Int, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryPostAdapter.HistoryPostViewHolder =
        HistoryPostViewHolder(
            ItemHistoryPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(
        holder: HistoryPostAdapter.HistoryPostViewHolder, position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<HistoryRecruitResponseDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HistoryPostViewHolder(private val binding: ItemHistoryPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(createdRecruitPostInfo: HistoryRecruitResponseDto) {
            requestManager.load("http://3.35.27.107:8080/images/${createdRecruitPostInfo.image}")
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgHistoryPostThumbnail)

            binding.tvHistoryPostTitle.text = createdRecruitPostInfo.title

            with(binding.tvHistoryPostState) {
                if (createdRecruitPostInfo.active) {
                    text = context.getString(R.string.history_post_status_progress)
                } else {
                    text = context.getString(R.string.history_post_status_success)
                    if (createdRecruitPostInfo.cancel)
                        text = context.getString(R.string.history_post_status_cancel)
                    if (createdRecruitPostInfo.fail)
                        text = context.getString(R.string.history_post_status_fail)
                }
            }

            binding.tvHistoryPostLocationStore.text = createdRecruitPostInfo.place
            binding.tvHistoryPostLocationUser.text = createdRecruitPostInfo.dormitory

            with(binding.tvHistoryPostCriterion) {
                text = when (createdRecruitPostInfo.criteria) {
                    RecruitFinishCriteria.NUMBER -> context.getString(R.string.finish_criterion_number_people)
                    RecruitFinishCriteria.PRICE -> context.getString(R.string.finish_criterion_price)
                    RecruitFinishCriteria.TIME -> context.getString(R.string.finish_criterion_time)
                }
            }

            binding.tvHistoryPostUploadDate.text =
                LocalDateTime.parse(createdRecruitPostInfo.createDate, formatter)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.layoutHistoryPost.setOnClickListener {
                    listener?.postClick(createdRecruitPostInfo.recruitId, pos)
                }
            }
        }
    }
}