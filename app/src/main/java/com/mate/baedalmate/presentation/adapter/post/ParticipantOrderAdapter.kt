package com.mate.baedalmate.presentation.adapter.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.databinding.ItemBulletPointTextviewBinding
import com.mate.baedalmate.databinding.ItemParticipantOrderBinding
import com.mate.baedalmate.domain.model.ParticipantMenuDto
import java.text.DecimalFormat

class ParticipantOrderAdapter(private val requestManager: RequestManager) :
    ListAdapter<ParticipantMenuDto, ParticipantOrderAdapter.ParticipantOrderViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ParticipantMenuDto>() {
            override fun areItemsTheSame(oldItem: ParticipantMenuDto, newItem: ParticipantMenuDto) = oldItem == newItem
            override fun areContentsTheSame(oldItem: ParticipantMenuDto, newItem: ParticipantMenuDto): Boolean =
                oldItem.userId == newItem.userId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantOrderViewHolder =
        ParticipantOrderViewHolder(
            ItemParticipantOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: ParticipantOrderAdapter.ParticipantOrderViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<ParticipantMenuDto>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class ParticipantOrderViewHolder(private val binding: ItemParticipantOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participantMenu: ParticipantMenuDto) {
            val decimalFormat = DecimalFormat("#,###")
            requestManager.load("http://3.35.27.107:8080/images/${participantMenu.profileImage}")
                .thumbnail(0.1f)
                .priority(Priority.HIGH)
                .centerCrop()
                .into(binding.imgParticipantOrderUserInfoThumbnail)
            binding.tvParticipantOrderUserInfoName.text = participantMenu.nickname

            for (i in 0 until participantMenu.menu.size) {
                val currentMenu = participantMenu.menu[i]
                val bulletTextViewBinding =
                    ItemBulletPointTextviewBinding.inflate(LayoutInflater.from(binding.layoutParticipantOrderMenuList.context))
                bulletTextViewBinding.message = "${currentMenu.name} / ${decimalFormat.format(currentMenu.quantity)}개 : ${decimalFormat.format(currentMenu.price)}원"
                val orderMenuTextView = bulletTextViewBinding.root
                binding.layoutParticipantOrderMenuList.addView(orderMenuTextView)
            }

            binding.tvParticipantOrderSumCurrent.text = "${decimalFormat.format(participantMenu.userOrderTotal)}원"
        }
    }
}