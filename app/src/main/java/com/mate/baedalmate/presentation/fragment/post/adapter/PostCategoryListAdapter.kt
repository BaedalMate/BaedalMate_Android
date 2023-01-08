package com.mate.baedalmate.presentation.fragment.post.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.TimeChangerUtil.getTimePassed
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.ItemPostCategoryListBinding
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import java.text.DecimalFormat
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
                oldItem.recruitId == newItem.recruitId

            override fun areContentsTheSame(
                oldItem: RecruitDto,
                newItem: RecruitDto,
            ): Boolean =
                oldItem == newItem
        }
        private val decimalFormat = DecimalFormat("#,###")
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
                durationMinuteCreated = getTimePassed(binding.layoutPostCategoryList.context, createTime, currentTime)
            }
            if (item.deadlineDate.isNotEmpty()) {
                val deadLineTime = LocalDateTime.parse(deadLineTimeString, formatter)
                val duration = Duration.between(currentTime, deadLineTime).toMinutes()
                durationMinuteDeadLine = duration.toString()
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.layoutPostCategoryList.setOnClickListener {
                    listener?.postClick(item.recruitId, pos)
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
            binding.tvPostCategoryListTimePassed.text = durationMinuteCreated
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
                    val goalText = "${decimalFormat.format(item.minPrice)}원 "
                    val currentText = " 현재 ${decimalFormat.format(item.currentPrice)}원"
                    binding.tvPostCategoryListContentsStateGoalTitle.text = "목표금액"
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
                RecruitFinishCriteria.TIME -> {
                    val deadLineTime = LocalDateTime.parse(deadLineTimeString, formatter)
                    val goalText = deadLineTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    val currentText = "${durationMinuteDeadLine}분 남음"
                    binding.tvPostCategoryListContentsStateGoalTitle.text = "마감시간"
                    binding.tvPostCategoryListContentsStateGoal.text = "$goalText | $currentText"
                    val span = SpannableString(binding.tvPostCategoryListContentsStateGoal.text)
                    span.setSpan(
                        StyleSpan(Typeface.BOLD),
                        goalText.length + 2,
                        goalText.length + 3 + currentText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    binding.tvPostCategoryListContentsStateGoal.text = span
                }
            }

            setParticipateCloseLayout(item.active, binding)
        }

        private fun setParticipateCloseLayout(
            isActive: Boolean,
            itemLayout: ItemPostCategoryListBinding
        ) {
            with(itemLayout) {
                if (isActive) {
                    layoutPostCategoryList.background = ContextCompat.getDrawable(
                        layoutPostCategoryList.context,
                        R.drawable.selector_post_category_list
                    )
                    imgPostCategoryList.imageTintList = null
                    tvPostCategoryListParticipateClose.visibility = View.GONE
                    tvPostCategoryListTitle.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListTitle.context,
                            R.color.black_000000
                        )
                    )
                    imgPostCategoryListLocationStore.imageTintList = null
                    tvPostCategoryListLocationStore.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListTitle.context,
                            R.color.black_000000
                        )
                    )
                    imgPostCategoryListLocationUser.imageTintList = null
                    tvPostCategoryListLocationUser.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListLocationUser.context,
                            R.color.black_000000
                        )
                    )
                    tvPostCategoryListContentsStateGoalTitle.backgroundTintList = null
                    tvPostCategoryListContentsStateGoal.visibility = View.VISIBLE
                } else {
                    layoutPostCategoryList.background = ContextCompat.getDrawable(
                        layoutPostCategoryList.context,
                        R.color.gray_main_C4C4C4
                    )
                    imgPostCategoryList.imageTintList = ContextCompat.getColorStateList(
                        imgPostCategoryList.context,
                        R.color.overlay_black_B3212123
                    )
                    tvPostCategoryListParticipateClose.visibility = View.VISIBLE
                    tvPostCategoryListTitle.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListTitle.context,
                            R.color.gray_dark_666666
                        )
                    )
                    imgPostCategoryListLocationStore.imageTintList =
                        ContextCompat.getColorStateList(
                            imgPostCategoryListLocationStore.context,
                            R.color.gray_dark_666666
                        )
                    tvPostCategoryListLocationStore.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListTitle.context,
                            R.color.gray_dark_666666
                        )
                    )
                    imgPostCategoryListLocationUser.imageTintList = ContextCompat.getColorStateList(
                        imgPostCategoryListLocationUser.context,
                        R.color.gray_dark_666666
                    )
                    tvPostCategoryListLocationUser.setTextColor(
                        ContextCompat.getColor(
                            tvPostCategoryListLocationUser.context,
                            R.color.gray_dark_666666
                        )
                    )
                    tvPostCategoryListContentsStateGoalTitle.backgroundTintList =
                        ContextCompat.getColorStateList(
                            tvPostCategoryListContentsStateGoalTitle.context,
                            R.color.gray_line_EBEBEB
                        )
                    tvPostCategoryListContentsStateGoal.visibility = View.GONE
                }
            }
        }
    }
}