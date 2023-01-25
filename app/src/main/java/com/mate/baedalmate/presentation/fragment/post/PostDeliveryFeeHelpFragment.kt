package com.mate.baedalmate.presentation.fragment.post

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.RoundDialogFragment
import com.mate.baedalmate.databinding.FragmentPostDeliveryFeeHelpBinding
import com.mate.baedalmate.databinding.ItemPostDeliveryFeeHelpDetailTextviewBinding
import com.mate.baedalmate.domain.model.ShippingFeeDto
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class PostDeliveryFeeHelpFragment : RoundDialogFragment() {
    private var binding by autoCleared<FragmentPostDeliveryFeeHelpBinding>()
    private val args by navArgs<PostDeliveryFeeHelpFragmentArgs>()
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogSizeRatio = 0.72f
        dialogHeightSizeRatio = 0.6f
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDeliveryFeeHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmClickListener()
        initData()
    }

    private fun confirmClickListener() {
        binding.tvPostDeliveryFeeHelpConfirm.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initData() {
        val deliveryFeeList = args.deliveryFeeList
        val isDeliveryFeeFreeVisible = deliveryFeeList.isEmpty()
        with(binding) {
            layoutPostDeliveryFeeHelpContents.visibility =
                if (isDeliveryFeeFreeVisible) View.INVISIBLE else View.VISIBLE
            tvPostDeliveryFeeHelpContentsEmpty.visibility =
                if (isDeliveryFeeFreeVisible) View.VISIBLE else View.GONE
            tvPostDeliveryFeeHelpCouponDetail.text =
                String.format(
                    getString(R.string.money_unit_korea),
                    decimalFormat.format(args.couponAmount)
                )

            for (deliveryFeeIdx in deliveryFeeList.indices) {
                val rangeView = setCurrentDeliveryFeeRangeView(
                    deliveryFeeIdx = deliveryFeeIdx,
                    deliveryFeeRanges = deliveryFeeList
                )
                this@with.layoutPostDeliveryFeeHelpContentsDetail.addView(rangeView)
            }
        }
    }

    private fun setCurrentDeliveryFeeRangeView(
        deliveryFeeIdx: Int,
        deliveryFeeRanges: Array<ShippingFeeDto>
    ): View {
        val rangeBinding =
            ItemPostDeliveryFeeHelpDetailTextviewBinding.inflate(LayoutInflater.from(context))
        with(rangeBinding) {
            setDeliveryRangeText(
                feeTextView = tvPostDeliveryFeeHelpDetailFee,
                rangeTextView = tvPostDeliveryFeeHelpDetailAmount,
                deliveryFeeIdx = deliveryFeeIdx,
                deliveryFeeRanges = deliveryFeeRanges
            )
            highlightCurrentRangeText(
                feeTextView = tvPostDeliveryFeeHelpDetailFee,
                rangeTextView = tvPostDeliveryFeeHelpDetailAmount,
                deliveryFeeIdx = deliveryFeeIdx,
                deliveryFeeRanges = deliveryFeeRanges
            )
        }
        return rangeBinding.root
    }

    private fun setDeliveryRangeText(
        feeTextView: TextView,
        rangeTextView: TextView,
        deliveryFeeIdx: Int,
        deliveryFeeRanges: Array<ShippingFeeDto>
    ) {
        val deliveryFee = String.format(
            getString(
                R.string.money_unit_korea,
                decimalFormat.format(deliveryFeeRanges[deliveryFeeIdx].shippingFee)
            )
        )
        feeTextView.text = deliveryFee

        val lowerPrice = String.format(
            getString(
                R.string.money_unit_korea,
                decimalFormat.format(deliveryFeeRanges[deliveryFeeIdx].lowerPrice)
            )
        )

        with(rangeTextView) {
            text =
                if (deliveryFeeIdx == deliveryFeeRanges.lastIndex)
                    "$lowerPrice ~ "
                else {
                    val upperPrice = String.format(
                        getString(
                            R.string.money_unit_korea,
                            decimalFormat.format(deliveryFeeRanges[deliveryFeeIdx + 1].lowerPrice)
                        )
                    )

                    "$lowerPrice ~ $upperPrice"
                }
        }
    }

    private fun highlightCurrentRangeText(
        feeTextView: TextView,
        rangeTextView: TextView,
        deliveryFeeIdx: Int,
        deliveryFeeRanges: Array<ShippingFeeDto>
    ) {
        if (args.currentShippingFee == deliveryFeeRanges[deliveryFeeIdx].shippingFee) {
            with(rangeTextView) { this.text = highlightRangeText(this.text.toString()) }
            with(feeTextView) { this.text = highlightRangeText(this.text.toString()) }
        }
    }

    private fun highlightRangeText(currentText: String): SpannableString {
        val currentSpan = SpannableString(currentText)
        currentSpan.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.main_FB5F1C)),
            0, currentSpan.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        currentSpan.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            currentSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return currentSpan
    }
}