package com.mate.baedalmate.presentation.adapter.write

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.databinding.ItemMenuSavedBinding
import java.text.DecimalFormat

class WriteFourthMenuListAdapter(
    private val items: MutableList<MenuDto>
) : RecyclerView.Adapter<WriteFourthMenuListAdapter.WriteFourthMenuListViewHolder>() {
    interface OnItemClickListener {
        fun deleteMenu(contents: MenuDto, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteFourthMenuListViewHolder {
        return WriteFourthMenuListViewHolder(
            ItemMenuSavedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WriteFourthMenuListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }

    inner class WriteFourthMenuListViewHolder(private val binding: ItemMenuSavedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: MenuDto, position: Int) {
            val decimalFormat = DecimalFormat("#,###")


            binding.tvMenuSavedTitle.text = contents.name
            binding.tvMenuSavedDescription.text =
                "${
                    decimalFormat.format(
                        contents.quantity.toString().replace(",", "").toDouble()
                    )
                }개 · ${
                    decimalFormat.format(
                        contents.price.toString().replace(",", "").toDouble()
                    )
                }원"

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnMenuSavedDelete.setOnClickListener {
                    if (contents != null) {
                        listener?.deleteMenu(contents, pos)
                    }
                }
            }
        }
    }
}