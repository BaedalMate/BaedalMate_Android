package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dialog.RoundDialogFragment
import com.mate.baedalmate.databinding.FragmentPostDeliveryFeeHelpBinding
import com.mate.baedalmate.databinding.ItemPostDeliveryFeeHelpDetailTextviewBinding
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
        binding.layoutPostDeliveryFeeHelpContents.visibility = if (isDeliveryFeeFreeVisible) View.INVISIBLE else View.VISIBLE
        binding.tvPostDeliveryFeeHelpContentsEmpty.visibility = if (isDeliveryFeeFreeVisible) View.VISIBLE else View.GONE

        for (deliveryFeeIdx in deliveryFeeList.indices) {
            val rangeBinding =
                ItemPostDeliveryFeeHelpDetailTextviewBinding.inflate(LayoutInflater.from(context))
            if(deliveryFeeIdx == deliveryFeeList.lastIndex) {
                rangeBinding.tvPostDeliveryFeeHelpDetailAmount.text =
                    "${decimalFormat.format(deliveryFeeList[deliveryFeeIdx].lowerPrice)}${getString(R.string.unit_korea)} ~ "
            } else {
                rangeBinding.tvPostDeliveryFeeHelpDetailAmount.text =
                    "${decimalFormat.format(deliveryFeeList[deliveryFeeIdx].lowerPrice)}${getString(R.string.unit_korea)} ~ ${
                        decimalFormat.format(deliveryFeeList[deliveryFeeIdx].upperPrice)
                    }${getString(R.string.unit_korea)}"
            }
            rangeBinding.tvPostDeliveryFeeHelpDetailFee.text =
                "${decimalFormat.format(deliveryFeeList[deliveryFeeIdx].shippingFee)}${getString(R.string.unit_korea)}"
            val rangeView = rangeBinding.root
            binding.layoutPostDeliveryFeeHelpContentsDetail.addView(rangeView)
        }

        binding.tvPostDeliveryFeeHelpCouponDetail.text =
            "${decimalFormat.format(args.couponAmount)}원"
    }
}