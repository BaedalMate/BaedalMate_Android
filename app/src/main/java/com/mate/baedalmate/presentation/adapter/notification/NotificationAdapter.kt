package com.mate.baedalmate.presentation.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.databinding.ItemNotificationBinding
import com.mate.baedalmate.domain.model.RecruitDto

class NotificationAdapter (private val requestManager: RequestManager) :
    ListAdapter<RecruitDto, NotificationAdapter.NotificationViewHolder>(diffCallback) {
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
    }

    interface OnItemClickListener {
        fun notificationClick(postId: Int, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationAdapter.NotificationViewHolder =
        NotificationViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: NotificationAdapter.NotificationViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<RecruitDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecruitDto) {
            // TODO 수정
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.layoutNotificationItem.setOnClickListener {
                    listener?.notificationClick(item.recruitId, pos)
                }
            }

//            requestManager.load("http://3.35.27.107:8080/images/${item.image}")
//                .thumbnail(0.1f)
//                .priority(Priority.HIGH)
//                .centerCrop()
//                .into(binding.imgNotificationThumbnail)
//            binding.tvNotificationTitle.text =
//            binding.tvNotificationDescription.text =
//            binding.tvNotificationTime.text =
        }
    }
}