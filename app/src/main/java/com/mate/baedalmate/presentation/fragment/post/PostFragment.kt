package com.mate.baedalmate.presentation.fragment.post

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
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
import com.mate.baedalmate.common.dialog.ConfirmAlertDialog
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.common.extension.navigateSafe
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.FragmentPostBinding
import com.mate.baedalmate.domain.model.DeliveryPlatform
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.domain.model.RecruitDetail
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
    private lateinit var closePostAlertDialog: AlertDialog
    private lateinit var cancelParticipatePostAlertDialog: AlertDialog
    private val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createClosePostAlertDialog()
        createCancelParticipatePostAlertDialog()
    }

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
        observeCloseRecruitPostState()
        observeCancelRecruitPostState()
        observeCancelParticipatePostState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ConfirmAlertDialog.hideConfirmDialog(closePostAlertDialog)
        ConfirmAlertDialog.hideConfirmDialog(cancelParticipatePostAlertDialog)
    }

    private fun createClosePostAlertDialog() {
        closePostAlertDialog = ConfirmAlertDialog.createChoiceDialog(
            context = requireContext(),
            title = getString(R.string.post_close_dialog_title),
            description = getString(R.string.post_close_dialog_description),
            confirmButtonFunction = { recruitViewModel.requestCloseRecruitPost(postId = args.postId) }
        )
    }

    private fun createCancelParticipatePostAlertDialog() {
        cancelParticipatePostAlertDialog = ConfirmAlertDialog.createChoiceDialog(
            context = requireContext(),
            title = getString(R.string.post_cancel_participate_dialog_title),
            description = getString(R.string.post_cancel_participate_dialog_description),
            confirmButtonFunction = { recruitViewModel.requestCancelParticipateRecruitPost(recruitId = args.postId) }
        )
    }

    private fun getPostDetailData() {
        showLoadingView()
        recruitViewModel.requestRecruitPost(postId = args.postId)
    }

    private fun setBackClickListener() {
        binding.btnPostActionbarBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun displayPostPlatform(platformName: String) {
        val platformImageDrawableId = when (platformName) {
            DeliveryPlatform.BAEMIN.name -> R.drawable.ic_baemin_logo
            DeliveryPlatform.COUPANG.name -> R.drawable.ic_coupang_logo
            DeliveryPlatform.DDGNGYO.name -> R.drawable.ic_ddangyo_logo
            DeliveryPlatform.YOGIYO.name -> R.drawable.ic_yogiyo_logo_circle
            else -> R.drawable.ic_etc_logo
        }
        binding.imgPostBackDeliveryPlatform.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                platformImageDrawableId
            )
        )
    }

    private fun setStoreLocationDetail(place: PlaceDto) {
        displayStoreLocationName(place.name)
        setStoreLocationDetailClickListener(place)
    }

    private fun displayStoreLocationName(placeName: String) {
        binding.tvPostFrontContentsInfoStoreDescription.text = placeName
    }

    private fun setStoreLocationDetailClickListener(place: PlaceDto) {
        binding.btnPostFrontContentsInfoStoreLocation.setOnDebounceClickListener {
            with(place) {
                findNavController().navigateSafe(
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
    }

    private fun displayUserScore(userScore: Float) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(3, 0, 0, 0)

        binding.layoutPostFrontUserScore.removeAllViews()
        for (i in starIndicator.indices) {
            starIndicator[i] = ImageView(requireContext())
            val calculateUserScore = userScore - 1
            if (i <= calculateUserScore) {
                starIndicator[i]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_star_full
                    )
                )
            } else if (i > calculateUserScore && (i - calculateUserScore) != 0f && (i - calculateUserScore) < 1) {
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
            starIndicator[i]!!.imageTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)
            starIndicator[i]!!.layoutParams = params
            binding.layoutPostFrontUserScore.addView(starIndicator[i])
        }

        val paramsTextView = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        paramsTextView.setMargins(6.dp, 0, 0, 0)
        val scoreTextView = TextView(requireContext())
        scoreTextView.apply {
            setTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.applesdgothic_neo_bold
                ), Typeface.NORMAL
            )
            text = String.format(getString(R.string.user_score_format), userScore)
            textSize = 12f
            lineHeight = 18
            layoutParams = paramsTextView
            gravity = Gravity.CENTER
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black_000000
                )
            )

            binding.layoutPostFrontUserScore.addView(this)
        }
    }

    private fun observeCancelRecruitPostState() {
        recruitViewModel.isCancelRecruitPostSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.post_alert_message_cancel),
                    Toast.LENGTH_SHORT
                ).show()
                getPostDetailData()
            }
        }
    }

    private fun observeCloseRecruitPostState() {
        recruitViewModel.isCloseRecruitPostSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.post_alert_message_close),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun observeCancelParticipatePostState() {
        recruitViewModel.isCancelParticipateRecruitPostSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.post_alert_message_cancel_participate),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun initContentsUserInfo(recruitDetail: RecruitDetail) {
        with(binding) {
            with(recruitDetail.userInfo) {
                glideRequestManager.load("http://3.35.27.107:8080/images/${profileImage}")
                    .override(45.dp)
                    .thumbnail(0.1f)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(imgPostFrontUser)
                displayUserScore(score)

                tvPostFrontUserName.text = nickname
                tvPostFrontUserDormitory.text = dormitory
            }
        }
    }

    private fun setDeadlineTime(deadlineDate: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val deadlineTimeString: String = deadlineDate
        if (deadlineDate.isNotEmpty()) {
            val deadlineTime = LocalDateTime.parse(deadlineTimeString, formatter)
            binding.tvPostFrontContentsDeadlineTime.text =
                String.format(
                    getString(R.string.post_deadline_time_format),
                    deadlineTime.hour,
                    deadlineTime.minute
                )
        }
    }

    private fun setDeadlinePeopleCount(minPeople: Int, currentPeople: Int) {
        binding.tvPostFrontContentsDeadlinePeople.text =
            String.format(
                getString(R.string.post_deadline_people_format),
                currentPeople,
                minPeople
            )
    }

    private fun initContentsPostInfo(recruitDetail: RecruitDetail) {
        with(binding) {
            initContentsPostInfoTitle(recruitDetail)
            initContentsPostInfoConditions(recruitDetail)
            tvPostFrontContentsInfoDetailDescription.text = recruitDetail.description
        }
    }

    private fun initContentsPostInfoTitle(recruitDetail: RecruitDetail) {
        with(binding) {
            tvPostFrontContentsTitle.text = recruitDetail.title
            viewPostFrontContentsClose.isVisible = !recruitDetail.active
        }
    }

    private fun initContentsPostInfoConditions(recruitDetail: RecruitDetail) {
        with(binding) {
            setDeadlineTime(recruitDetail.deadlineDate)
            tvPostFrontContentsDeadlineDeliveryFee.text =
                String.format(
                    getString(R.string.unit_korea_with_money),
                    decimalFormat.format(recruitDetail.shippingFee)
                )
            setDeadlinePeopleCount(
                minPeople = recruitDetail.minPeople,
                currentPeople = recruitDetail.currentPeople
            )
            binding.tvPostFrontContentsDormitory.text = recruitDetail.dormitory
        }
    }

    private fun setRecruitActionButton(recruitDetail: RecruitDetail) {
        // 활성화 - 미참여 - 참여자   > 모집 참여하기
        // 활성화 - 참여 - 참여자    > 채팅방이동/모집나가기
        // 활성화 - 참여 - 주최자    > 채팅방이동/모집마감
        //비활성화 - 미참여 - 참여자  > 마감된 모집글입니다
        //비화렁화 - 참여 - 참여자   > 채팅방 이동
        //비활성화 - 참여 - 주최자   > 채팅방 이동
        displayRecruitActionButtonTwoButton(recruitDetail.participate && recruitDetail.active)
        if (!recruitDetail.participate) {
            if (recruitDetail.active) {
                with(binding.btnPostFrontContentsParticipateOneButton) {
                    text = getString(R.string.post_participate_in)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white_FFFFFF))
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_btn_main_orange_gray_light_radius_10
                    )
                    isEnabled = true
                    setOnDebounceClickListener {
                        findNavController().navigateSafe(
                            PostFragmentDirections.actionPostFragmentToPostMenuBottomSheetDialogFragment(
                                recruitId = args.postId
                            )
                        )
                    }
                }
            } else {
                with(binding.btnPostFrontContentsParticipateOneButton) {
                    text = getString(R.string.post_not_active)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_main_C4C4C4))
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_btn_main_orange_gray_light_radius_10
                    )
                    isEnabled = false
                    // 클릭해도 아무런 동작하지 않도록 처리
                    setOnDebounceClickListener { }
                }
            }
        } else {
            if (recruitDetail.active) {
                with(binding.btnPostFrontContentsParticipateTwoButtonLeft) {
                    isEnabled = true
                    setOnDebounceClickListener {
                        // TODO 받아온 채팅방 ID로 parameter 변경
                        navigateToChatRoom(chatRoomId = 0)
                    }
                }
                setRecruitActionButtonTwoButtonRight(isHost = recruitDetail.host)

            } else {
                with(binding.btnPostFrontContentsParticipateOneButton) {
                    text = getString(R.string.post_navigate_to_chatroom)
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.line_orange_FFA077
                        )
                    )
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_btn_enabled_stroke_orange_line_white_gray_light_radius_10
                    )
                    isEnabled = true
                    setOnDebounceClickListener {
                        // TODO 받아온 채팅방 ID로 parameter 변경
                        navigateToChatRoom(chatRoomId = 0)
                    }
                }
            }
        }
    }

    private fun displayRecruitActionButtonTwoButton(shouldDisplayTwoButton: Boolean) {
        with(binding) {
            btnPostFrontContentsParticipateOneButton.isVisible = !shouldDisplayTwoButton
            layoutPostFrontContentsParticipateTwoButton.isVisible = shouldDisplayTwoButton
        }
    }

    private fun setRecruitActionButtonTwoButtonRight(isHost: Boolean) {
        with(binding.btnPostFrontContentsParticipateTwoButtonRight) {
            if (isHost) {
                isEnabled = true
                text = getString(R.string.post_close)
                setOnDebounceClickListener {
                    ConfirmAlertDialog.showConfirmDialog(closePostAlertDialog)
                    ConfirmAlertDialog.resizeDialogFragment(
                        requireContext(),
                        closePostAlertDialog,
                        dialogSizeRatio = 0.7f
                    )
                }
            } else {
                isEnabled = true
                text = getString(R.string.post_participate_out)
                setOnDebounceClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        ConfirmAlertDialog.showConfirmDialog(cancelParticipatePostAlertDialog)
                        ConfirmAlertDialog.resizeDialogFragment(
                            requireContext(),
                            cancelParticipatePostAlertDialog,
                            dialogSizeRatio = 0.7f
                        )
                    }
                }
            }
        }
    }

    private fun initContents() {
        with(binding) {
            starIndicator = arrayOfNulls<ImageView>(5)

            viewLifecycleOwner.lifecycleScope.launch {
                recruitViewModel.recruitPostDetail.observe(viewLifecycleOwner) { recruitDetail ->
                    if (recruitDetail != null) {
                        hideLoadingView()
                        glideRequestManager.load("http://3.35.27.107:8080/images/${recruitDetail.image}")
                            .override(GetDeviceSize.getDeviceWidthSize(requireContext()))
                            .thumbnail(0.1f)
                            .priority(Priority.HIGH)
                            .centerCrop()
                            .into(imgPostBack)

                        displayPostPlatform(recruitDetail.platform)
                        setStoreLocationDetail(recruitDetail.place)
                        initContentsUserInfo(recruitDetail)
                        initContentsPostInfo(recruitDetail)
                        setRecruitActionButton(recruitDetail)
                        setOptionClickListener(recruitDetail)
                        requestRecruitPostDetailForModify(recruitDetail)
                        setModifyPostClickListener(recruitDetail)
                    }
                }
            }
        }
    }

    private fun setOptionClickListener(recruitDetail: RecruitDetail) {
        with(binding.btnPostActionbarOption) {
            isVisible = !(recruitDetail.host && !recruitDetail.active)
            setOnDebounceClickListener {
                findNavController().navigateSafe(
                    PostFragmentDirections.actionPostFragmentToPostOptionFragment(
                        isHost = recruitDetail.host,
                        postId = args.postId,
                        postWriterName = recruitDetail.userInfo.nickname,
                        postWriterUserId = recruitDetail.userInfo.userId.toInt()
                    )
                )
            }
        }
    }

    private fun requestRecruitPostDetailForModify(recruitDetail: RecruitDetail) {
        if (recruitDetail.host) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    recruitViewModel.requestRecruitPostForModify(postId = args.postId)
                }
            }
        }
    }

    private fun setModifyPostClickListener(recruitDetail: RecruitDetail) {
        with(binding.tvPostFrontContentsDetailModify) {
            /*
            visibility = if (recruitDetail.host && recruitDetail.active) View.VISIBLE else View.GONE
            recruitViewModel.recruitPostDetailForModify.observe(viewLifecycleOwner) { recruitDetailForModify ->
                setOnDebounceClickListener {
                    findNavController().navigateSafe(
                        PostFragmentDirections.actionPostFragmentToModifyPostFragment(
                            recruitDetailForModify
                        )
                    )
                }
            }
             */
        }
    }

    private fun navigateToChatRoom(chatRoomId: Int) {
        findNavController().navigateSafe(
            PostFragmentDirections.actionPostFragmentToChatFragment(
                roomId = chatRoomId,
            )
        )
    }

    private fun showLoadingView() {
        with(binding) {
            layoutPostBack.visibility = View.GONE
            layoutPostFront.visibility = View.GONE
            lottiePostLoading.visibility = View.VISIBLE
        }
    }

    private fun hideLoadingView() {
        with(binding) {
            layoutPostBack.visibility = View.VISIBLE
            layoutPostFront.visibility = View.VISIBLE
            lottiePostLoading.visibility = View.GONE
        }
    }
}