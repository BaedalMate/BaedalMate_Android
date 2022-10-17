package com.mate.baedalmate.presentation.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomInfo
import com.mate.baedalmate.databinding.ItemChatListBinding

class ChatRoomListAdapter :
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
            // binding.tvChatListTitle = info.lastMessage // TODO 현재 방 제목이 넘어오지 않음
            binding.tvChatListTitle.text = info.lastMessage.sender // 임시로 보낸 사람으로 적용
            // binding.imgChatList // TODO 현재 해당 방의 이미지가 넘어오지 않음

            binding.tvChatListMessage.text = info.lastMessage.message
            binding.tvChatListTimePassed.text = info.lastMessage.sendDate // TODO: 현재 시간으로부터 계산 필요
        }
    }
}