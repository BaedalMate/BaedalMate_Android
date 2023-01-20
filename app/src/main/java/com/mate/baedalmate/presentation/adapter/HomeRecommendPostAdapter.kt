package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitDto
import com.mate.baedalmate.databinding.ItemHomeBottomPostRecommendBinding
import com.mate.baedalmate.presentation.fragment.home.HomeFragmentDirections
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeRecommendPostAdapter(private val requestManager: RequestManager) :
    ListAdapter<MainRecruitDto, HomeRecommendPostAdapter.HomeRecommendPostViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MainRecruitDto>() {
            override fun areItemsTheSame(oldItem: MainRecruitDto, newItem: MainRecruitDto) =
                oldItem.recruitId == newItem.recruitId

            override fun areContentsTheSame(
                oldItem: MainRecruitDto,
                newItem: MainRecruitDto
            ): Boolean =
                oldItem == newItem
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

    override fun submitList(list: MutableList<MainRecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeRecommendPostViewHolder(private val binding: ItemHomeBottomPostRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: MainRecruitDto) {
            val decimalFormat = DecimalFormat("#,###")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val deadlineTimeString: String = post.deadlineDate
            var durationMinute = "0"

            if (post.deadlineDate != "") {
                val deadlineTime = LocalDateTime.parse(deadlineTimeString, formatter)
                val currentTime = LocalDateTime.now()
                val duration = Duration.between(currentTime, deadlineTime).toMinutes()
                durationMinute = duration.toString()
            }

            with(binding) {
                requestManager.load("http://3.35.27.107:8080/images/${post.image}")
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgHomeBottomPostRecommendItem)

                tvHomeBottomPostRecommendItemInfoTitle.text = "${post.place}"
                tvHomeBottomPostRecentItemTopPerson.text = "${post.currentPeople}/${post.minPeople}"
                tvHomeBottomPostRecentItemTopTime.text = "${durationMinute}분"
                tvHomeBottomPostRecommendItemInfoBottomDeliveryCurrent.text =
                    " ${decimalFormat.format(post.shippingFee)}원"
                tvHomeBottomPostRecommendItemInfoBottomCurrent.text =
                    " ${decimalFormat.format(post.minPrice)}원"
                tvHomeBottomPostRecommendItemInfoBottomUser.text =
                    "${post.username} · ${post.dormitory} ★ ️${post.userScore}"

                root.setOnClickListener {
                    NavHostFragment.findNavController(binding.root.findFragment()).navigate(
                        HomeFragmentDirections.actionHomeFragmentToPostFragment(
                            postId = post.recruitId
                        )
                    )
                }
                setParticipateCloseLayout(post.active, this)
            }

        }

        private fun setParticipateCloseLayout(
            isActive: Boolean,
            itemLayout: ItemHomeBottomPostRecommendBinding
        ) {
            with(itemLayout) {
                if (isActive) {
                    layoutHomeBottomPostRecommendParticipateClose.visibility = View.GONE
                } else {
                    layoutHomeBottomPostRecommendParticipateClose.visibility = View.VISIBLE
                    tvHomeBottomPostRecentItemTopTime.text =
                        tvHomeBottomPostRecentItemTopTime.context.getString(R.string.participate_complete)
                }
            }
        }
    }
}