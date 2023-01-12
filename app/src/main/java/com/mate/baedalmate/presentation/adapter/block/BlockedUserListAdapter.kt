package com.mate.baedalmate.presentation.adapter.block

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.data.datasource.remote.block.BlockedUserDto
import com.mate.baedalmate.databinding.ItemBlockUserBinding

class BlockedUserListAdapter(private val requestManager: RequestManager) :
    ListAdapter<BlockedUserDto, BlockedUserListAdapter.BlockedUserListViewHolder>(diffCallback) {
    interface OnItemClickListener {
        fun unblockBlockedUser(userId: Int, userName: String, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BlockedUserDto>() {
            override fun areItemsTheSame(oldItem: BlockedUserDto, newItem: BlockedUserDto) =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: BlockedUserDto,
                newItem: BlockedUserDto
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUserListViewHolder =
        BlockedUserListViewHolder(
            ItemBlockUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: BlockedUserListAdapter.BlockedUserListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<BlockedUserDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class BlockedUserListViewHolder(private val binding: ItemBlockUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(blockedUserInfo: BlockedUserDto) {
            with(binding) {
                requestManager.load("http://3.35.27.107:8080/images/${blockedUserInfo.profileImage}")
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgBlockUserThumbnail)
                tvBlockUserNickname.text = blockedUserInfo.nickname

                btnBlockUserUnblock.setOnDebounceClickListener {
                    listener?.unblockBlockedUser(blockedUserInfo.userId, blockedUserInfo.nickname, pos = adapterPosition)
                }
            }
        }
    }
}