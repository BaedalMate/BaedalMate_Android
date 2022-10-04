package com.mate.baedalmate.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.R
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.databinding.ItemHomeTopPostBinding


class HomeTopPostAdapter :
    ListAdapter<RecruitDto, HomeTopPostAdapter.HomeTopPostViewHolder>(
        diffCallback
    ) {
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

    override fun submitList(list: MutableList<RecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }


    inner class HomeTopPostViewHolder(private val binding: ItemHomeTopPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: RecruitDto, position: Int) {
            var homeTopPostTagAdapter = HomeTopPostTagAdapter()
            binding.rvHomeTopPostTag.adapter = homeTopPostTagAdapter

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val constraintLayout: ConstraintLayout = binding.layoutHomeTopPostContents

            if (position == itemCount - 1) {
                homeTopPostTagAdapter.submitList(mutableListOf("글 작성하기"))

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
                binding.layoutHomeTopPostContentsInformationUser.visibility = View.INVISIBLE
            } else {
                // TODO 태그 리스트 서버 연동
                homeTopPostTagAdapter.submitList(mutableListOf("간편식", "배달팁 무료"))

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
                binding.layoutHomeTopPostContentsInformationUser.visibility = View.VISIBLE
            }

            binding.tvHomeTopPostContentsInformationTitle.text = post.title
            binding.deliveryFee = post.deliveryFee
            binding.minPrice = post.minPrice
            // TODO: 배달 예상 시간 어디에 넣을 지
            binding.tvHomeTopPostContentsInformationUserStar.text = post.userScore.toString()
            // TODO: 게시글의 이미지 넣기

        }
    }

    fun checkMoneyUnit(money: Int): Int {
        if (money < 1000) return money
        else {
            return 0
        }
    }
}