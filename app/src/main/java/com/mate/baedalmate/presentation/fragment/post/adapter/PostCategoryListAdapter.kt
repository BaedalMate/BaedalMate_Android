package com.mate.baedalmate.presentation.fragment.post.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.ItemPostCategoryListBinding
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostCategoryListAdapter(private val requestManager: RequestManager) :
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
        fun postClick(postId: Int, pos: Int)
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
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val createdTimeString: String = item.createDate
            val deadLineTimeString: String = item.deadlineDate
            var durationMinuteCreated = "0"
            var durationMinuteDeadLine = "0"
            val currentTime = LocalDateTime.now()

            if (item.createDate.isNotEmpty()) {
                val createTime = LocalDateTime.parse(createdTimeString, formatter)
                val duration = Duration.between(createTime, currentTime).toMinutes()
                durationMinuteCreated = duration.toString()
            }
            if (item.deadlineDate.isNotEmpty()) {
                val deadLineTime = LocalDateTime.parse(deadLineTimeString, formatter)
                val duration = Duration.between(currentTime, deadLineTime).toMinutes()
                durationMinuteDeadLine = duration.toString()
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.layoutPostCategoryList.setOnClickListener {
                    listener?.postClick(item.id, pos)
                }
            }

            requestManager.load("http://3.35.27.107:8080/images/${item.image}")
                .thumbnail(0.1f)
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgPostCategoryList)
            binding.tvPostCategoryListTitle.text = item.title
            binding.tvPostCategoryListLocationStore.text = item.place
            binding.tvPostCategoryListLocationUser.text = item.dormitory
            binding.tvPostCategoryListTimePassed.text = "${durationMinuteCreated}분 전"
            when (item.criteria) {
                RecruitFinishCriteria.NUMBER -> {
                    val goalText = "${item.minPeople}인 모집 "
                    val currentText = " 현재인원 ${item.currentPeople}인"
                    binding.tvPostCategoryListContentsStateGoalTitle.text = "최소인원"
                    binding.tvPostCategoryListContentsStateGoal.text = "$goalText|$currentText"
                    val span = SpannableString(binding.tvPostCategoryListContentsStateGoal.text)
                    span.setSpan(
                        StyleSpan(Typeface.BOLD),
                        goalText.length + 1,
                        goalText.length + 1 + currentText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    binding.tvPostCategoryListContentsStateGoal.text = span
                }
                RecruitFinishCriteria.PRICE -> {
                    // TODO: 백엔드측 수정이후 반영필요
//                    val goalText = "${item.minPeople}원 "
//                    val currentText = " 현재 ${item.currentPeople}원"
                    binding.tvPostCategoryListContentsStateGoalTitle.text = "최소주문"
//                    binding.tvPostCategoryListContentsStateGoal.text = "$goalText|$currentText"
//                    val span = SpannableString(binding.tvPostCategoryListContentsStateGoal.text)
//                    span.setSpan(
//                        StyleSpan(Typeface.BOLD),
//                        goalText.length + 1,
//                        goalText.length + 1 + currentText.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    binding.tvPostCategoryListContentsStateGoal.text = span
                }
                RecruitFinishCriteria.TIME -> {
                    val goalText = "${item.deadlineDate} "
                    val currentText = " ${durationMinuteDeadLine}분 남음"
                    binding.tvPostCategoryListContentsStateGoalTitle.text = "마감시간"
                    binding.tvPostCategoryListContentsStateGoal.text = "$goalText|$currentText"
                    val span = SpannableString(binding.tvPostCategoryListContentsStateGoal.text)
                    span.setSpan(
                        StyleSpan(Typeface.BOLD),
                        goalText.length + 1,
                        goalText.length + 1 + currentText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    binding.tvPostCategoryListContentsStateGoal.text = span
                }
            }
        }
    }
}