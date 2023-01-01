package com.mate.baedalmate.presentation.adapter.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.ItemParticipantBinding
import com.mate.baedalmate.domain.model.ParticipantDto

class ParticipantListAdapter(private val requestManager: RequestManager) :
    ListAdapter<ParticipantDto, ParticipantListAdapter.ParticipantListViewHolder>(diffCallback) {
    interface OnItemClickListener {
        fun setUserProfileClickListener(userInfo: ParticipantDto, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ParticipantDto>() {
            override fun areItemsTheSame(oldItem: ParticipantDto, newItem: ParticipantDto) =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ParticipantDto,
                newItem: ParticipantDto
            ): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantListViewHolder =
        ParticipantListViewHolder(
            ItemParticipantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: ParticipantListAdapter.ParticipantListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<ParticipantDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class ParticipantListViewHolder(private val binding: ItemParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participant: ParticipantDto) {
            requestManager.load("http://3.35.27.107:8080/images/${participant.profileImage}")
                .thumbnail(0.1f)
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgParticipantThumbnail)
            binding.tvParticipantName.text = participant.nickname

            binding.layoutParticipant.setOnDebounceClickListener {
                listener?.setUserProfileClickListener(
                    userInfo = participant,
                    pos = adapterPosition
                )
            }
        }
    }
}