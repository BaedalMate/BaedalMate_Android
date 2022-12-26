package com.mate.baedalmate.presentation.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.databinding.FragmentParticipantsOrderListBinding
import com.mate.baedalmate.presentation.adapter.post.ParticipantOrderAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class ParticipantsOrderListFragment : Fragment() {
    private var binding by autoCleared<FragmentParticipantsOrderListBinding>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val args by navArgs<ParticipantsOrderListFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var participantOrderAdapter: ParticipantOrderAdapter
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantsOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAllMenuList()
        setParticipantsOrderListAdapter()
        setParticipantsOrderListBottomPadding()
    }

    private fun getAllMenuList() {
        chatViewModel.getAllMenuList(args.roomId)
    }

    private fun setParticipantsOrderListAdapter() {
        participantOrderAdapter = ParticipantOrderAdapter(requestManager = glideRequestManager)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        with(binding.rvParticipantsOrderListUsers) {
            this.layoutManager = layoutManager
            this.adapter = participantOrderAdapter
        }
        observeParticipantsOrderList()
    }

    private fun observeParticipantsOrderList() {
        chatViewModel.allMenuList.observe(viewLifecycleOwner) { menuList ->
            participantOrderAdapter.submitList(menuList.participants.toMutableList())
            with(binding) {
                val moneyUnit = getString(R.string.unit_korea)
                tvParticipantsOrderListSumAmountCurrent.text = "${decimalFormat.format(menuList.allOrderTotal)}${moneyUnit}"
                tvParticipantsOrderListMineReceiptSumMenuCurrent.text = "${decimalFormat.format(menuList.myOrderPrice)}${moneyUnit}"
                tvParticipantsOrderListMineReceiptDeliveryFeeOrigin.text = "${decimalFormat.format(menuList.shippingFee)}${moneyUnit}"
                tvParticipantsOrderListMineReceiptDeliveryFeeCurrent.text = "${decimalFormat.format(menuList.shippingFeePerParticipant)}${moneyUnit}"
                tvParticipantsOrderListMineReceiptCouponCurrent.text = "${decimalFormat.format(menuList.coupon)}${moneyUnit}"
                tvParticipantsOrderListMineFinalAmountCurrent.text = "${decimalFormat.format(menuList.myPaymentPrice)}${moneyUnit}"
            }
        }
    }

    private fun setParticipantsOrderListBottomPadding() {
        // TODO 유저 자신의 정보가 나오는 창의 크기만큼 padding을 정상적으로 추가하는지 확인 필요
        binding.layoutParticipantsOrderListContents.setPadding(
            0,
            0,
            0,
            (25 + binding.layoutParticipantsOrderListMine.height).dp
        )
    }
}