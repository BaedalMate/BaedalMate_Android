package com.mate.baedalmate.presentation.fragment.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.google.gson.Gson
import com.mate.baedalmate.R
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.KeyboardVisibilityUtils
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.dp
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomRecruitDetailDto
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.data.di.StompModule
import com.mate.baedalmate.databinding.FragmentChatBinding
import com.mate.baedalmate.domain.model.ApiErrorStatus
import com.mate.baedalmate.domain.model.MessageInfo
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.presentation.adapter.chat.ChatAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import com.mate.baedalmate.presentation.viewmodel.ReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var binding by autoCleared<FragmentChatBinding>()
    private var stompModule = StompModule()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val reviewViewModel by activityViewModels<ReviewViewModel>()
    private val args by navArgs<ChatFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var currentUser: UserInfoResponse? = null

    private var chatMessageLogList = mutableListOf<MessageInfo>()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val decimalFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        getChatLog()
        setSTOMP()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHideKeyboard()
        setBackClickListener()
        initCurrentUser()
        initChatRoom()
        setReceiveMessage()
        setMessageInputClickListener()
        setMessageSendClickListener()
        observeReviewStateCallback()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        stompModule.disconnectStomp()
    }

    private fun setHideKeyboard() {
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), requireView())

        var startClickTime = 0L;
        binding.rvChatLog.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    startClickTime = System.currentTimeMillis()
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                        // Touch was a simple tap. Do whatever.
                        HideKeyBoardUtil.hide(requireActivity())
                    } else {
                        // Touch was a not a simple tap.
                    }
                }
                return false
                // true로 설정시, touch event가 consume됨
            }
        })
    }

    private fun setBackClickListener() {
        binding.btnChatActionbarBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentUser = memberViewModel.getUserInfo()
            }
        }
    }

    private fun getChatLog() {
        chatViewModel.getChatRoomDetailLog(args.roomId)
    }

    private fun setSTOMP() {
        stompModule.connectStomp(
            accessToken = memberViewModel.getAccessToken(),
            roomId = args.roomId
        )
    }

    private fun initChatRoom() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.chatRoomLog.observe(viewLifecycleOwner) { detail ->
                    val recruitDetail = Gson().fromJson(
                        detail.recruit.toString(),
                        ChatRoomRecruitDetailDto::class.java
                    )
                    initChatRoomTitleBarInfo(recruitDetail)
                    setInfoActionClickListener(recruitDetail, detail.reviewed)
                    setOptionClickListener(recruitDetail)
                    initChatLog(detail.messages)
                }
            }
        }
    }

    private fun initChatLog(chatMessages: List<MessageInfo>) {
        chatAdapter = ChatAdapter(
            requestManager = glideRequestManager,
            userName = currentUser?.nickname ?: ""
        )
        binding.rvChatLog.adapter = chatAdapter

        chatMessageLogList = chatMessages.toMutableList()
        chatMessageLogList.removeIf { message -> message.message.trim().isEmpty() }
        chatAdapter.submitList(chatMessageLogList)
        binding.rvChatLog.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun initChatRoomTitleBarInfo(recruitInfo: ChatRoomRecruitDetailDto) {
        val createdTimeString = recruitInfo.createDate
        val createdTime = LocalDateTime.parse(createdTimeString, formatter)
        navigateToRecruitPost(binding.imgChatInfo, recruitInfo.recruitId)
        navigateToRecruitPost(binding.layoutChatInfoContents, recruitInfo.recruitId)

        binding.tvChatInfoContentsCreatedDate.text =
            with(createdTime) { "${this.year}년 ${this.monthValue + 1}월 ${this.dayOfMonth}일 ${this.hour}시 ${this.minute}분" }
        binding.tvChatInfoContentsTitle.text = recruitInfo.title

        glideRequestManager.load("http://3.35.27.107:8080/images/${recruitInfo.recruitImage}")
            .priority(Priority.HIGH)
            .centerCrop()
            .into(binding.imgChatInfo)

        when (recruitInfo.criteria) {
            RecruitFinishCriteria.TIME -> {
                val currentTime = LocalDateTime.now()
                val deadLineTimeString: String = recruitInfo.deadlineDate
                var durationMinuteDeadLine = "0"

                if (recruitInfo.deadlineDate.isNotEmpty()) {
                    val deadLineTime =
                        LocalDateTime.parse(deadLineTimeString, formatter)
                    val duration =
                        Duration.between(currentTime, deadLineTime).toMinutes()
                    durationMinuteDeadLine = duration.toString()
                }

                binding.tvChatInfoContentsCriterionTitle.text = "마감시간"
                binding.tvChatInfoContentsCriterionDetail.text =
                    if (durationMinuteDeadLine.toInt() < 0) getString(R.string.participate_close)
                    else "${
                        decimalFormat.format(
                            durationMinuteDeadLine.toInt()
                        )
                    }분 남음"
            }
            RecruitFinishCriteria.NUMBER -> {
                binding.tvChatInfoContentsCriterionTitle.text = "최소인원"
                binding.tvChatInfoContentsCriterionDetail.text =
                    "${decimalFormat.format(recruitInfo.minPeople)}인"
            }
            RecruitFinishCriteria.PRICE -> {
                binding.tvChatInfoContentsCriterionTitle.text = "목표금액"
                binding.tvChatInfoContentsCriterionDetail.text =
                    "${decimalFormat.format(recruitInfo.minPrice)}원"
            }
        }
    }

    private fun navigateToRecruitPost(view: View, recruitId: Int) {
        view.setOnDebounceClickListener {
            findNavController().navigate(ChatFragmentDirections.actionChatFragmentToPostFragment(
                postId = recruitId
            ))
        }
    }

    private fun setReceiveMessage() {
        stompModule.chatMessage.observe(viewLifecycleOwner) {
            val currentTimeString =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            if (it.message.trim() != "")
                chatMessageLogList.add(
                    MessageInfo(
                        messageId = 0,
                        message = it.message.trim(),
                        sendDate = currentTimeString,
                        sender = it.sender,
                        senderId = it.senderId,
                        senderImage = it.senderImage
                    )
                )
            chatAdapter.submitList(chatMessageLogList)
            binding.rvChatLog.smoothScrollToPosition(chatMessageLogList.size - 1)
        }
    }

    private fun setMessageInputClickListener() {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                binding.rvChatLog.run {
                    smoothScrollBy(
                        scrollX,
                        scrollY + keyboardHeight - binding.layoutChatUserInput.height + 15.dp
                    )
                }
            }
        )
    }

    private fun setMessageSendClickListener() {
        HideKeyBoardUtil.hideEditText(requireContext(), binding.etChatUserInput)

        binding.etChatUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.trim().isNullOrEmpty()) {
                    binding.btnChatUserInputSend.imageTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.gray_main_C4C4C4)
                } else {
                    binding.btnChatUserInputSend.imageTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.main_FB5F1C)
                }
            }
        })

        binding.btnChatUserInputSend.setOnClickListener {
            if (binding.etChatUserInput.text.trim().isNotEmpty()) {
                stompModule.sendMessage(
                    roomId = args.roomId.toLong(),
                    message = binding.etChatUserInput.text.trim().toString(),
                    senderId = currentUser?.userId?.toInt() ?: 0
                )
                binding.etChatUserInput.setText("")
                binding.btnChatUserInputSend.imageTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.gray_main_C4C4C4)
            }
        }
    }

    private fun setInfoActionClickListener(
        recruitInfo: ChatRoomRecruitDetailDto,
        isReviewed: Boolean
    ) {
        if (recruitInfo.active) {
            with(binding.btnChatInfoAction) {
                text = getString(R.string.chat_info_action_change_menu)
                setOnDebounceClickListener {
                    findNavController().navigate(
                        ChatFragmentDirections.actionChatFragmentToChangeOrderFragment(recruitId = recruitInfo.recruitId)
                    )
                }
            }
        } else {
            with(binding.btnChatInfoAction) {
                text = getString(R.string.chat_info_action_change_review)
                setOnDebounceClickListener {
                    findNavController().navigate(
                        ChatFragmentDirections.actionChatFragmentToReviewUserFragment(recruitId = recruitInfo.recruitId)
                    )
                }
                if (isReviewed)
                    this.isEnabled = false
            }
        }
    }

    private fun observeReviewStateCallback() {
        reviewViewModel.isReviewSubmitSuccess.observe(viewLifecycleOwner) {
            val isSuccess = it.getContentIfNotHandled()

            if (isSuccess == ApiErrorStatus.RESPONSE_SUCCESS) {
                Toast.makeText(
                    requireContext(),
                    R.string.review_submit_toast_success,
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            } else if (isSuccess == ApiErrorStatus.RESPONSE_FAIL_DUPLICATE) {
                Toast.makeText(
                    requireContext(),
                    R.string.review_submit_toast_fail_duplicate,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (isSuccess == ApiErrorStatus.RESPONSE_FAIL_UNKNOWN) {
                Toast.makeText(
                    requireContext(),
                    R.string.review_submit_toast_fail_unknown,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setOptionClickListener(recruitInfo: ChatRoomRecruitDetailDto) {
        binding.btnChatActionbarOption.setOnDebounceClickListener {
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToParticipantListFragment(
                    recruitId = recruitInfo.recruitId
                )
            )
        }
    }
}