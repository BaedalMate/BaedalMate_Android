package com.mate.baedalmate.presentation.adapter.write

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.data.datasource.remote.write.Place
import com.mate.baedalmate.databinding.ItemWriteSecondPlaceBinding

class WriteSecondPlaceDialogListAdapter : ListAdapter<Place, WriteSecondPlaceDialogListAdapter.WriteSecondPlaceDialogListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Place>() {
            override fun areItemsTheSame(oldItem: Place, newItem: Place) = oldItem == newItem
            override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun selectStore(contents: Place, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteSecondPlaceDialogListViewHolder =
        WriteSecondPlaceDialogListViewHolder(
            ItemWriteSecondPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: WriteSecondPlaceDialogListAdapter.WriteSecondPlaceDialogListViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Place>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class WriteSecondPlaceDialogListViewHolder(private val binding: ItemWriteSecondPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: Place) {
            binding.tvWriteSecondPlaceTitle.text = contents.place_name
            binding.tvWriteSecondPlaceDescription.text = contents.road_address_name

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnWriteSecondPlaceSelect.setOnClickListener {
                    if (contents != null) {
                        listener?.selectStore(contents, pos)
                    }
                }
            }
        }
    }
}