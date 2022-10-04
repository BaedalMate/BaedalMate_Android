package com.mate.baedalmate.presentation.fragment.write

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.data.datasource.remote.recruit.MenuDto
import com.mate.baedalmate.databinding.FragmentWriteFourthBinding
import com.mate.baedalmate.presentation.adapter.write.WriteFourthMenuListAdapter
import com.mate.baedalmate.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class WriteFourthFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFourthBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var writeFourthMenuListAdapter: WriteFourthMenuListAdapter
    private var currentMenuAmount = 0
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFourthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackClickListener()
        setNextClickListener()
        setAddMenuClickListener()
        setMenuListOriginalValue()
    }

    private fun setBackClickListener() {
        binding.btnWriteFourthActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setNextClickListener() {
        binding.btnWriteFourthNext.setOnClickListener {
            // TODO
        }
    }

    private fun setAddMenuClickListener() {
        binding.btnWriteFourthMenuAdd.setOnClickListener {
            findNavController().navigate(R.id.action_writeFourthFragment_to_writeFourthAddMenuFragment)
        }
    }

    private fun setMenuListOriginalValue() {
        writeViewModel.menuList.observe(viewLifecycleOwner) {
            setMenuListAdapter(menuList = it)

            if (!it.isNullOrEmpty()) {
                writeFourthMenuListAdapter.notifyDataSetChanged()
                binding.btnWriteFourthNext.isEnabled = true
            } else {
                binding.btnWriteFourthNext.isEnabled = false
            }
            setMenuDeleteClickListener()
            setMenuTotalAmount(menuList = it)
        }
    }

    private fun setMenuListAdapter(menuList: MutableList<MenuDto>) {
        writeFourthMenuListAdapter = WriteFourthMenuListAdapter(menuList)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        with(binding.rvWriteFourthAddList) {
            this.layoutManager = layoutManager
            this.adapter = writeFourthMenuListAdapter
        }
    }

    private fun setMenuDeleteClickListener() {
        writeFourthMenuListAdapter.setOnItemClickListener(object :
            WriteFourthMenuListAdapter.OnItemClickListener {
            override fun deleteMenu(contents: MenuDto, pos: Int) {
                writeViewModel.menuList.removeAt(pos)
                writeFourthMenuListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun setMenuTotalAmount(menuList: MutableList<MenuDto>) {
        var result = ""

        currentMenuAmount = 0 // 메뉴 총 금액
        for (menu in menuList) {
            val menuAmount = menu.price
            currentMenuAmount += (menuAmount * menu.quantity)
        }

        result = decimalFormat.format(currentMenuAmount.toString().replace(",", "").toDouble())
        binding.tvWriteFourthAmountDetailTotalCurrent.text = "${result}원"

        val currentDeliveryFee = setDeliveryFee(currentMenuAmount) // 배달비 설정
        binding.tvWriteFourthAmountDetailDeliveryFeeCurrent.text =
            "${decimalFormat.format(currentDeliveryFee)}원"

        val totalAmount = decimalFormat.format(
            (currentMenuAmount + currentDeliveryFee).toString().replace(",", "").toDouble()
        )
        binding.tvWriteFourthAmountDetailTotal.text = "${totalAmount}원"
        initCoupon(totalAmount = currentMenuAmount + currentDeliveryFee)
    }

    private fun setDeliveryFee(currentMenuAmount: Int): Int {
        // WriteFirstFragment에서 정한 배달비 구간 별로 배달비 자동 조정 설정
        var currentDeliveryFee = 0
        val deliveryFeeRangeList = writeViewModel.deliveryFeeRangeList
        if (!writeViewModel.isDeliveryFeeFree) {
            for (feeRange in deliveryFeeRangeList) {
                if (currentMenuAmount <= feeRange.upperPrice && feeRange.lowerPrice <= currentMenuAmount) {
                    currentDeliveryFee = feeRange.shippingFee
                }
            }
        }
        return currentDeliveryFee
    }

    private fun initCoupon(totalAmount: Int) {
        var discountedAmount = totalAmount
        if (writeViewModel.isCouponUse) {
            var result = ""
            val couponAmount = writeViewModel.couponAmount
            discountedAmount = totalAmount - couponAmount
            result = decimalFormat.format(couponAmount.toString().replace(",", "").toDouble())
            binding.tvWriteFourthAmountDetailCouponCurrent.text =
                "${result}원" // 이전 Fragment에서 설정한 쿠폰 사용금액
        } else {
            binding.tvWriteFourthAmountDetailCouponCurrent.text = "0원"
        }

        if (discountedAmount <= 0) {
            binding.tvWriteFourthAmountDetailDiscounted.text = "0원"
            setTotalAmount(totalAmount = 0)
        } else {
            binding.tvWriteFourthAmountDetailDiscounted.text =
                "${decimalFormat.format(discountedAmount)}원" // 총 금액에서 쿠폰 금액을 뺀 금액, 사실상 최종 결제 금액
            setTotalAmount(totalAmount = discountedAmount)
        }
    }

    private fun setTotalAmount(totalAmount: Int) {
        binding.tvWriteFourthAmountTotal.text = "${decimalFormat.format(totalAmount)}원" // 최종 결제금액
        val span = SpannableString(binding.tvWriteFourthAmountTotal.text)
        span.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.black_000000)),
            span.length.minus(1),
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvWriteFourthAmountTotal.text = span
    }
}