package com.mate.baedalmate.presentation.adapter.write

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.R
import com.mate.baedalmate.common.ListLiveData
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.ItemWriteFirstDeliveryFeeRangeBinding
import com.mate.baedalmate.domain.model.ShippingFeeDto

class WriteFirstDeliveryFeeRangeAdapter(
    var items: MutableList<ShippingFeeDto>,
    var deliveryFeeRangeCorrectList: ListLiveData<Boolean>,
    var deliveryFeeRangeEmptyChkList: ListLiveData<Boolean>
) :
    RecyclerView.Adapter<WriteFirstDeliveryFeeRangeAdapter.WriteFirstDeliveryFeeRangeViewHolder>() {
    interface OnItemClickListener {
        fun removeItem(contents: ShippingFeeDto, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteFirstDeliveryFeeRangeViewHolder =
        WriteFirstDeliveryFeeRangeViewHolder(
            ItemWriteFirstDeliveryFeeRangeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        holder: WriteFirstDeliveryFeeRangeAdapter.WriteFirstDeliveryFeeRangeViewHolder,
        position: Int,
    ) {
        val item = items[position]
        holder.bind(item, position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class WriteFirstDeliveryFeeRangeViewHolder(private val binding: ItemWriteFirstDeliveryFeeRangeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contents: ShippingFeeDto, position: Int) {
            with(binding) {
                tvDeliveryFeeRangeTitle.text = "구간 ${bindingAdapterPosition + 1}"
                etDeliveryFeeRange.setText("${contents.lowerPrice}")
                etDeliveryFee.setText("${contents.shippingFee}")

                if (bindingAdapterPosition == 0) btnDeliveryFeeRangeDelete.visibility = View.GONE
                btnDeliveryFeeRangeDelete.setOnDebounceClickListener {
                    with(etDeliveryFee) {
                        clearTextChangedListeners()
                        text = null
                    }
                    with(etDeliveryFeeRange) {
                        clearTextChangedListeners()
                        text = null
                    }
                    listener?.removeItem(contents, bindingAdapterPosition)
                }
                etDeliveryFee.isEnabled = (bindingAdapterPosition == items.size - 1)
                etDeliveryFeeRange.isEnabled = (bindingAdapterPosition == items.size - 1)
                etDeliveryFeeRange.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val changedRange = ShippingFeeDto(
                            if (s?.toString().isNullOrEmpty()) 0 else s?.toString()?.toInt() ?: 0,
                            items[bindingAdapterPosition].shippingFee,
                            items[bindingAdapterPosition].upperPrice
                        )
                        items[bindingAdapterPosition] = changedRange

                        deliveryFeeRangeEmptyChkList.replaceAt(
                            bindingAdapterPosition,
                            !(binding.etDeliveryFeeRange.text.isNullOrEmpty() || binding.etDeliveryFee.text.isNullOrEmpty())
                        )

                        if (s.toString().isNotEmpty() && bindingAdapterPosition != 0) {
                            if (items[bindingAdapterPosition - 1].lowerPrice >= // 직전의 range
                                items[bindingAdapterPosition].lowerPrice
                            ) {
                                binding.etDeliveryFeeRange.background = ContextCompat.getDrawable(
                                    binding.etDeliveryFeeRange.context,
                                    R.drawable.background_white_radius_10_stroke_red
                                )
                                deliveryFeeRangeCorrectList.replaceAt(bindingAdapterPosition, false)
                            } else {
                                binding.etDeliveryFeeRange.background = ContextCompat.getDrawable(
                                    binding.etDeliveryFeeRange.context,
                                    R.drawable.selector_edittext_white_gray_line_radius_10
                                )
                                deliveryFeeRangeCorrectList.replaceAt(bindingAdapterPosition, true)
                            }
                        }
                    }
                })
                etDeliveryFee.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        val changedRange = ShippingFeeDto(
                            items[bindingAdapterPosition].lowerPrice,
                            if (s?.toString().isNullOrEmpty()) 0 else s?.toString()?.toInt() ?: 0,
                            items[bindingAdapterPosition].upperPrice
                        )
                        items[bindingAdapterPosition] = changedRange

                        deliveryFeeRangeEmptyChkList.replaceAt(
                            bindingAdapterPosition,
                            !(binding.etDeliveryFeeRange.text.isNullOrEmpty() || binding.etDeliveryFee.text.isNullOrEmpty())
                        )
                    }
                })
            }
        }
    }
}