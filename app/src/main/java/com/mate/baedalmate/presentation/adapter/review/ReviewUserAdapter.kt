package com.mate.baedalmate.presentation.adapter.review

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.ItemReviewUserBinding
import com.mate.baedalmate.domain.model.ParticipantDto

class ReviewUserAdapter(private val requestManager: RequestManager) :
    ListAdapter<ParticipantDto, ReviewUserAdapter.ReviewUserViewHolder>(diffCallback) {
    interface OnItemClickListener {
        fun setUserScore(score: Float, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ParticipantDto>() {
            override fun areItemsTheSame(oldItem: ParticipantDto, newItem: ParticipantDto) =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: ParticipantDto,
                newItem: ParticipantDto
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewUserViewHolder =
        ReviewUserViewHolder(
            ItemReviewUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: ReviewUserAdapter.ReviewUserViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<ParticipantDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class ReviewUserViewHolder(private val binding: ItemReviewUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participantInfo: ParticipantDto) {
            with(binding) {
                requestManager.load("http://3.35.27.107:8080/images/${participantInfo.profileImage}")
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgReviewUserThumbnail)

                tvReviewUserName.text = participantInfo.nickname

                val pos = adapterPosition
                for (starButton in layoutReviewUserScore.children) {
                    displayUserScore(starButton as ImageButton, pos)
                }
            }
        }

        private fun displayUserScore(starButton: ImageButton, pos: Int) {
            starButton.setOnDebounceClickListener {
                val currentButtonIndex =
                    (starButton.parent as ConstraintLayout).indexOfChild(starButton)

                for (idx in 0..currentButtonIndex) {
                    val starButton = binding.layoutReviewUserScore.getChildAt(idx)
                    starButton.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_star_full)
                }

                for (idx in currentButtonIndex + 1 until binding.layoutReviewUserScore.childCount) {
                    val starButton = binding.layoutReviewUserScore.getChildAt(idx)
                    starButton.background =
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_star_empty)
                }

                listener?.setUserScore(currentButtonIndex + 1.0f, pos)
            }
        }
    }
}