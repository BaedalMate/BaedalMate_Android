package com.mate.baedalmate.presentation.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.databinding.ItemChatMineBinding
import com.mate.baedalmate.databinding.ItemChatOtherBinding
import com.mate.baedalmate.domain.model.MessageInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatAdapter(private val requestManager: RequestManager, private val userName: String) :
    ListAdapter<MessageInfo, RecyclerView.ViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MessageInfo>() {
            override fun areItemsTheSame(oldItem: MessageInfo, newItem: MessageInfo) =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: MessageInfo,
                newItem: MessageInfo
            ): Boolean =
                oldItem == newItem
        }
        private const val TYPE_MINE = 0
        private const val TYPE_OTHER = 1
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MINE -> {
                ChatViewMineHolder(
                    ItemChatMineBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ChatViewOtherHolder(
                    ItemChatOtherBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ChatViewMineHolder) {
            holder.bind(getItem(position))
        } else if (holder is ChatViewOtherHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun submitList(list: MutableList<MessageInfo>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).sender == userName) {
            TYPE_MINE
        } else {
            TYPE_OTHER
        }
    }

    inner class ChatViewOtherHolder(private val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageInfo) {
            val sendTimeString = message.sendDate
            val sendTime = LocalDateTime.parse(sendTimeString, formatter)

            with(binding) {
                tvChatOtherMessage.text = message.message
                tvChatOtherTime.text = sendTime.format(DateTimeFormatter.ofPattern("a h:mm"))
                tvChatOtherName.text = message.sender
                requestManager.load("${message.senderImage}")
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgChatOther)
            }
        }
    }

    inner class ChatViewMineHolder(private val binding: ItemChatMineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageInfo) {
            val sendTimeString = message.sendDate
            val sendTime = LocalDateTime.parse(sendTimeString, formatter)

            binding.tvChatMineMessage.text = message.message
            binding.tvChatMineTime.text = sendTime.format(DateTimeFormatter.ofPattern("a h:mm"))
        }
    }
}