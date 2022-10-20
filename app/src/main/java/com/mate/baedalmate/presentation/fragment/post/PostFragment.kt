package com.mate.baedalmate.presentation.fragment.post

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
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

                        when (recruitDetail.platform) {
                            DeliveryPlatform.BAEMIN.name -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_baemin_circle
                                    )
                                )
                            }
                            DeliveryPlatform.COUPANG.name -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_coupang_circle
                                    )
                                )
                            }
                            DeliveryPlatform.DDGNGYO.name -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_ddangyo_circle
                                    )
                                )
                            }
                            DeliveryPlatform.YOGIYO.name -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_yogiyo_circle
                                    )
                                )
                            }
                            else -> {
                                imgPostBackDeliveryPlatform.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_etc_circle
                                    )
                                )
                            }
                        }

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

                        val paramsTextView = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        paramsTextView.setMargins(5.dp, 0, 0, 0)
                        val scoreTextView = TextView(requireContext())
                        scoreTextView.setTypeface(
                            ResourcesCompat.getFont(
                                requireContext(),
                                R.font.applesdgothic_neo_bold
                            ), Typeface.NORMAL
                        )
                        scoreTextView.text = "${recruitDetail.score}"
                        scoreTextView.layoutParams = paramsTextView
                        scoreTextView.gravity = Gravity.CENTER
                        scoreTextView.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.main_FB5F1C
                            )
                        )
                        binding.layoutPostFrontUserScore.addView(scoreTextView)

                        tvPostFrontUserName.text = recruitDetail.username
                        tvPostFrontUserDormitory.text = recruitDetail.userDormitory

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
                                )
                            )
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
                            btnPostFrontContentsParticipate.isEnabled = false // TODO 모집 마감하기 기능 미구현 상태이므로 임시 추가
                            btnPostFrontContentsParticipate.setOnClickListener {
                                // TODO 모집 마감 네트워킹 코드 삽입
                            }
                        } else {
                            btnPostFrontContentsParticipate.text =
                                getString(R.string.post_participate_in)
                            if (recruitDetail.participate) {
                                btnPostFrontContentsParticipate.text =
                                    getString(R.string.post_participate_out)
                                btnPostFrontContentsParticipate.isEnabled = false // TODO 모집 나가기 기능 미구현 상태이므로 임시 추가
                                // TODO 모집 나가기 네트워킹 코드 삽입
                            } else {
                                btnPostFrontContentsParticipate.text =
                                    getString(R.string.post_participate_in)
                                binding.btnPostFrontContentsParticipate.setOnClickListener {
                                    findNavController().navigate(
                                        PostFragmentDirections.actionPostFragmentToPostMenuBottomSheetDialogFragment(
                                            roomId = args.postId
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}