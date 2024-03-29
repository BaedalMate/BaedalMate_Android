package com.mate.baedalmate.presentation.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.TimeChangerUtil
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomInfo
import com.mate.baedalmate.databinding.ItemChatListBinding
import com.mate.baedalmate.presentation.fragment.chat.ChatListFragmentDirections
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatRoomListAdapter(private val requestManager: RequestManager) :
    ListAdapter<ChatRoomInfo, ChatRoomListAdapter.ChatRoomListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatRoomInfo>() {
            override fun areItemsTheSame(oldItem: ChatRoomInfo, newItem: ChatRoomInfo) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: ChatRoomInfo, newItem: ChatRoomInfo): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomListViewHolder =
        ChatRoomListViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: ChatRoomListAdapter.ChatRoomListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<ChatRoomInfo>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class ChatRoomListViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: ChatRoomInfo) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val lastMessageSendTimeString: String = info.lastMessage.sendDate

            binding.tvChatListTitle.text = info.title
            requestManager.load("http://3.35.27.107:8080/images/${info.image}")
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgChatList)

            binding.tvChatListMessage.text = info.lastMessage.message

            if (info.lastMessage.sendDate != "") {
                val lastMessageSendTime = LocalDateTime.parse(lastMessageSendTimeString, formatter)
                val currentTime = LocalDateTime.now()
                val timePassed = TimeChangerUtil.getTimePassed(binding.tvChatListTimePassed.context, lastMessageSendTime, currentTime)
                binding.tvChatListTimePassed.text = timePassed
            }

            binding.root.setOnClickListener {
                NavHostFragment.findNavController(binding.root.findFragment())
                    .navigate(ChatListFragmentDirections.actionChatListFragmentToChatFragment(roomId = info.chatRoomId))
            }
        }
    }
}