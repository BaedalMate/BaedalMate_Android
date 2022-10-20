package com.mate.baedalmate.presentation.fragment.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mate.baedalmate.common.HideKeyBoardUtil
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.data.di.StompModule
import com.mate.baedalmate.databinding.FragmentChatBinding
import com.mate.baedalmate.domain.model.MessageInfo
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.presentation.adapter.chat.ChatAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
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
    private val args by navArgs<ChatFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var chatAdapter: ChatAdapter

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
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), view)
        setBackClickListener()
        initChatLog()
        setReceiveMessage()
        setMessageSendClickListener()
        setInfoActionClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        stompModule.disconnectStomp()
    }

    private fun setBackClickListener() {
        binding.btnChatActionbarBack.setOnClickListener {
            findNavController().navigateUp()
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

    private fun initChatLog() {
        chatAdapter = ChatAdapter(
            requestManager = glideRequestManager,
            userName = memberViewModel.userInfo.value?.nickname.toString()
        )
        binding.rvChatLog.adapter = chatAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatRoomLog.observe(viewLifecycleOwner) { detail ->
                    chatMessageLogList = detail.messages.toMutableList()
                    chatAdapter.submitList(chatMessageLogList)
                    binding.rvChatLog.scrollToPosition(chatAdapter.itemCount - 1)

                    val createdTimeString = detail.recruit.createDate
                    val createdTime = LocalDateTime.parse(createdTimeString, formatter)

                    binding.tvChatInfoContentsCreatedDate.text =
                        with(createdTime) { "${this.year}년 ${this.monthValue + 1}월 ${this.dayOfMonth}일 ${this.hour}시 ${this.minute}분" }
                    binding.tvChatInfoContentsTitle.text = detail.recruit.title

                    glideRequestManager.load("http://3.35.27.107:8080/images/${detail.recruit.recruitImage}")
                        .priority(Priority.HIGH)
                        .centerCrop()
                        .into(binding.imgChatInfo)

                    when (detail.recruit.criteria) {
                        RecruitFinishCriteria.TIME -> {
                            val currentTime = LocalDateTime.now()
                            val deadLineTimeString: String = detail.recruit.deadlineDate
                            var durationMinuteDeadLine = "0"

                            if (detail.recruit.deadlineDate.isNotEmpty()) {
                                val deadLineTime =
                                    LocalDateTime.parse(deadLineTimeString, formatter)
                                val duration =
                                    Duration.between(currentTime, deadLineTime).toMinutes()
                                durationMinuteDeadLine = duration.toString()
                            }

                            binding.tvChatInfoContentsCriterionTitle.text = "마감시간"
                            binding.tvChatInfoContentsCriterionDetail.text =
                                "${decimalFormat.format(durationMinuteDeadLine.toInt())}분 남음"
                        }
                        RecruitFinishCriteria.NUMBER -> {
                            binding.tvChatInfoContentsCriterionTitle.text = "최소인원"
                            binding.tvChatInfoContentsCriterionDetail.text =
                                "${decimalFormat.format(detail.recruit.minPeople)}인"
                        }
                        RecruitFinishCriteria.PRICE -> {
                            binding.tvChatInfoContentsCriterionTitle.text = "목표금액"
                            binding.tvChatInfoContentsCriterionDetail.text =
                                "${decimalFormat.format(detail.recruit.minPrice)}원"
                        }
                    }
                }
            }
        }
    }

    private fun setReceiveMessage() {
        stompModule.chatMessage.observe(viewLifecycleOwner) {
            val currentTimeString =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            chatMessageLogList.add(
                MessageInfo(
                    id = 0, // TODO 메시지 ID 수정
                    message = it.message.trim(),
                    sendDate = currentTimeString,
                    sender = it.sender,
                    senderImage = it.senderImage
                )
            )
            chatAdapter.submitList(chatMessageLogList)
            binding.rvChatLog.smoothScrollToPosition(chatMessageLogList.size - 1)
        }
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
                    senderId = memberViewModel.userInfo.value?.userId ?: 0
                )
                binding.etChatUserInput.setText("")
                binding.btnChatUserInputSend.imageTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.gray_main_C4C4C4)
            }
        }
    }

    private fun setInfoActionClickListener() {
        binding.btnChatInfoAction.setOnClickListener {
            // TODO 메뉴 변경/후기 작성하기 기능 추가
        }
    }
}