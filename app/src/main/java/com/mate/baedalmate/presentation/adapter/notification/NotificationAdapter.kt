package com.mate.baedalmate.presentation.adapter.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.TimeChangerUtil
import com.mate.baedalmate.data.datasource.remote.notification.Notification
import com.mate.baedalmate.databinding.ItemNotificationBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationAdapter(private val requestManager: RequestManager) :
    ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(
                oldItem: Notification,
                newItem: Notification,
            ) =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: Notification,
                newItem: Notification,
            ): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun notificationClick(roomId: Int, pos: Int)
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

    override fun submitList(list: MutableList<Notification>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val createTime = LocalDateTime.parse(item.createDate, formatter)
            val pos = adapterPosition

            with(binding) {
                if (pos != RecyclerView.NO_POSITION) {
                    layoutNotificationItem.setOnClickListener {
                        listener?.notificationClick(item.chatRoomId, pos)
                    }
                }

                requestManager.load("http://3.35.27.107:8080/images/${item.image}")
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgNotificationThumbnail)
                tvNotificationTitle.text = item.title
                tvNotificationDescription.text = item.body
                tvNotificationTime.text = TimeChangerUtil.getTimePassed(
                    tvNotificationTime.context,
                    createTime,
                    LocalDateTime.now()
                )
            }
        }
    }
}