package com.mate.baedalmate.presentation.fragment.post

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.databinding.FragmentPostBinding
import com.mate.baedalmate.domain.model.DeliveryPlatform
import com.mate.baedalmate.domain.model.ShippingFeeDto
import com.mate.baedalmate.presentation.viewmodel.RecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()
    private val args by navArgs<PostFragmentArgs>()
    private val recruitViewModel by activityViewModels<RecruitViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var starIndicator: Array<ImageView?>
    private var deliveryFeeList = listOf<ShippingFeeDto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPostDetailData()
        setBackClickListener()
        initContents()
    }

    private fun getPostDetailData() {
        recruitViewModel.requestRecruitPost(postId = args.postId)
    }

    private fun setBackClickListener() {
        binding.btnPostActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initContents() {
        with(binding) {
            val decimalFormat = DecimalFormat("#,###")
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            starIndicator = arrayOfNulls<ImageView>(5)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(3, 0, 0, 0)

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    recruitViewModel.recruitPostDetail.observe(viewLifecycleOwner) { recruitDetail ->
                        glideRequestManager.load("http://3.35.27.107:8080/images/${recruitDetail.image}")
                            .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
                            .thumbnail(0.1f)
                            .priority(Priority.HIGH)
                            .centerCrop()
                            .into(imgPostBack)

                        /*
                        // TODO 플랫폼 여부 확인 필요
                        when(플랫폼) {
                            DeliveryPlatform.BAEMIN -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baemin_circle))
                            }
                            DeliveryPlatform.COUPANG -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_coupang_circle))
                            }
                            DeliveryPlatform.DDGNGYO -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_ddangyo_circle))
                            }
                            DeliveryPlatform.YOGIYO -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_yogiyo_circle))
                            }
                            DeliveryPlatform.ETC -> {
                                // TODO ETC 이미지 설정
//                                imgPostBackDeliveryPlatform.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baemin_circle))
                            }
                        }
                        */

                        imgPostBackStore.setOnClickListener {
                            with(recruitDetail.place) {
                                findNavController().navigate(
                                    PostFragmentDirections.actionPostFragmentToPostMapFragment(
                                        name = this.name,
                                        addressName = this.addressName,
                                        roadAddressName = this.roadAddressName,
                                        x = this.x,
                                        y = this.y
                                    )
                                )
                            }
                        }

                        glideRequestManager.load(recruitDetail.profileImage)
                            .override(45.dp)
                            .thumbnail(0.1f)
                            .priority(Priority.HIGH)
                            .centerCrop()
                            .into(imgPostFrontUser)

                        binding.layoutPostFrontUserScore.removeAllViews()
                        for (i in starIndicator.indices) {
                            starIndicator[i] = ImageView(requireContext())
                            if (i <= recruitDetail.score) {
                                starIndicator[i]!!.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_star_full
                                    )
                                )
                            } else if (i > recruitDetail.score && (i - recruitDetail.score) != 0f && (i - recruitDetail.score) < 1) {
                                starIndicator[i]!!.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_star_half
                                    )
                                )
                            } else {
                                starIndicator[i]!!.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_star_empty
                                    )
                                )
                            }
                            starIndicator[i]!!.layoutParams = params
                            binding.layoutPostFrontUserScore.addView(starIndicator[i])
                        }

                        tvPostFrontUserName.text = recruitDetail.username
                        // TODO: 유저 정보의 Dormitory 설정 필요
//                        tvPostFrontUserDormitory.text = recruitDetail.dormitory.name

                        tvPostFrontContentsTitle.text = recruitDetail.title

                        val deadlineTimeString: String = recruitDetail.deadlineDate
                        if (recruitDetail.deadlineDate.isNotEmpty()) {
                            val deadlineTime = LocalDateTime.parse(deadlineTimeString, formatter)
                            tvPostFrontContentsDeadlineTime.text =
                                "${deadlineTime.hour}시 ${deadlineTime.minute}분"
                        }

                        deliveryFeeList = recruitDetail.shippingFeeDetail
                        btnPostFrontContentsDeadlineDeliveryFeeHelp.setOnClickListener {
                            findNavController().navigate(
                                PostFragmentDirections.actionPostFragmentToPostDeliveryFeeHelpFragment(
                                    deliveryFeeList = deliveryFeeList.toTypedArray(),
                                    couponAmount = recruitDetail.coupon
                                ))
                        }

                        tvPostFrontContentsDeadlineDeliveryFee.text =
                            "${decimalFormat.format(recruitDetail.shippingFee)}원"
                        tvPostFrontContentsDeadlinePeople.text =
                            "${recruitDetail.currentPeople}명 / ${recruitDetail.minPeople}명"
                        tvPostFrontContentsDormitory.text = recruitDetail.dormitory
                        tvPostFrontContentsDetail.text = recruitDetail.description

                        btnPostFrontContentsParticipate.isEnabled = recruitDetail.active
                        if (recruitDetail.host) {
                            btnPostFrontContentsParticipate.text = "모집 마감하기"
                            btnPostFrontContentsParticipate.setOnClickListener {
                                // TODO 모집 마감 네트워킹 코드 삽입
                            }
                        } else {
                            btnPostFrontContentsParticipate.text =
                                getString(R.string.post_participate_in)
                            // TODO: 참가중인지 체크하는 조건 추가 필요
//                            if(tmp.participate) {
//                                btnPostFrontContentsParticipate.text = getString(R.string.post_participate_out)
                            // TODO 모집 나가기 네트워킹 코드 삽입
//                            } else {
//                                btnPostFrontContentsParticipate.text = getString(R.string.post_participate_in)
                            // TODO 모집 참여하기 네트워킹 코드 삽입
//                            }
                        }
                    }
                }
            }
        }
    }
}