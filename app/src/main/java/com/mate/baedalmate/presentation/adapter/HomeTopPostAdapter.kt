package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.R
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitDto
import com.mate.baedalmate.databinding.ItemHomeTopPostBinding
import com.mate.baedalmate.domain.model.TagDto
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeTopPostAdapter :
    ListAdapter<TagRecruitDto, HomeTopPostAdapter.HomeTopPostViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<TagRecruitDto>() {
            override fun areItemsTheSame(
                oldItem: TagRecruitDto,
                newItem: TagRecruitDto,
            ) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TagRecruitDto,
                newItem: TagRecruitDto,
            ): Boolean =
                oldItem == newItem
        }
    }

    val originalItemCount = super.getItemCount()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeTopPostViewHolder =
        HomeTopPostViewHolder(
            ItemHomeTopPostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(
        holder: HomeTopPostAdapter.HomeTopPostViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), position)
    }

    override fun submitList(list: MutableList<TagRecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }


    inner class HomeTopPostViewHolder(private val binding: ItemHomeTopPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: TagRecruitDto, position: Int) {
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

            var homeTopPostTagAdapter = HomeTopPostTagAdapter()
            binding.rvHomeTopPostTag.adapter = homeTopPostTagAdapter

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val constraintLayout: ConstraintLayout = binding.layoutHomeTopPostContents

            if (position == itemCount - 1) {
                homeTopPostTagAdapter.submitList(mutableListOf(TagDto("글 작성하기")))

                val writeConstraintSet = ConstraintSet()
                writeConstraintSet.clone(constraintLayout)
                writeConstraintSet.connect(
                    R.id.img_home_top_post_contents,
                    ConstraintSet.END,
                    R.id.guideline_home_top_post_contents_divide_write,
                    ConstraintSet.END,
                    0
                )
                writeConstraintSet.applyTo(constraintLayout)

                binding.layoutHomeTopPostContentsInformation.visibility = View.INVISIBLE
                binding.layoutHomeTopPostContentsWrite.visibility = View.VISIBLE
                binding.layoutImgHomeTopPostContentsWrite.visibility = View.VISIBLE
                binding.imgHomeTopPostContentsWrite.visibility = View.VISIBLE
                binding.layoutHomeTopPostContentsInformationUser.visibility = View.INVISIBLE
            } else {
                homeTopPostTagAdapter.submitList(post.tags.toMutableList())

                val postConstraintSet = ConstraintSet()
                postConstraintSet.clone(constraintLayout)
                postConstraintSet.connect(
                    R.id.img_home_top_post_contents,
                    ConstraintSet.END,
                    R.id.guideline_home_top_post_contents_divide,
                    ConstraintSet.END,
                    0
                )
                postConstraintSet.applyTo(constraintLayout)

                binding.layoutHomeTopPostContentsInformation.visibility = View.VISIBLE
                binding.layoutHomeTopPostContentsWrite.visibility = View.GONE
                binding.layoutImgHomeTopPostContentsWrite.visibility = View.GONE
                binding.imgHomeTopPostContentsWrite.visibility = View.GONE
                binding.layoutHomeTopPostContentsInformationUser.visibility = View.VISIBLE
            }

            binding.tvHomeTopPostContentsInformationTitle.text = post.place
            binding.tvHomeTopPostContentsInformationDescriptionDeliveryCurrent.text = " ${decimalFormat.format(post.shippingFee)}원"
            binding.tvHomeTopPostContentsInformationDescriptionMinCurrent.text = " ${decimalFormat.format(post.minPrice)}원"
            binding.tvHomeTopPostContentsInformationDescriptionTime.text = "${durationMinute}분 남음"
            binding.tvHomeTopPostContentsInformationUserStar.text = post.userScore.toString()
            // TODO: 게시글의 이미지 넣기

            binding.layoutHomeTopPostContentsWrite.setOnClickListener {
                findNavController(binding.layoutHomeTopPostContentsWrite.findFragment()).navigate(R.id.action_homeFragment_to_writeCategoryFragment)
            }
            binding.layoutImgHomeTopPostContentsWrite.setOnClickListener {
                findNavController(binding.layoutHomeTopPostContentsWrite.findFragment()).navigate(R.id.action_homeFragment_to_writeCategoryFragment)
            }
        }
    }

    fun checkMoneyUnit(money: Int): Int {
        if (money < 1000) return money
        else {
            return 0
        }
    }
}