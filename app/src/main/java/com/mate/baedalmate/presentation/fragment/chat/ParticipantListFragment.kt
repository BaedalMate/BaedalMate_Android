package com.mate.baedalmate.presentation.fragment.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mate.baedalmate.R
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.databinding.FragmentParticipantListBinding
import com.mate.baedalmate.domain.model.ParticipantDto
import com.mate.baedalmate.presentation.adapter.chat.ParticipantListAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParticipantListFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentParticipantListBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val args by navArgs<ParticipantListFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var participantListAdapter: ParticipantListAdapter
    private var currentUserInfo = UserInfoResponse("", "", "", 0f, 0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserInfo()
        observeUserInfo()
        getChatParticipants()
        setParticipantListAdapter()
        setCheckMenuClickListener()
    }

    // TODO 내 정보를 체크하는 것을 API를 다시 불러오는 방법이 아닌 로컬 정보를 활용하거나 참여자 API에서 받아오는 쪽으로 추후 변경 필요
    private fun getUserInfo() {
        memberViewModel.requestUserInfo()
    }

    private fun observeUserInfo() {
        memberViewModel.userInfo.observe(viewLifecycleOwner) {
            currentUserInfo = it
        }
    }

    private fun getChatParticipants() {
        chatViewModel.getChatParticipants(id = args.recruitId)
    }

    private fun setParticipantListAdapter() {
        participantListAdapter = ParticipantListAdapter(requestManager = glideRequestManager)
        with(binding.rvReviewUserList) {
            this.adapter = participantListAdapter
        }
        observeParticipantList()
        setUserProfileClickListener()
    }

    private fun observeParticipantList() {
        chatViewModel.chatParticipants.observe(viewLifecycleOwner) {
            participantListAdapter.submitList(it.participants.toMutableList())
        }
    }

    private fun setUserProfileClickListener() {
        participantListAdapter.setOnItemClickListener(object : ParticipantListAdapter.OnItemClickListener {
            override fun setUserProfileClickListener(userInfo: ParticipantDto, pos: Int) {
                if (currentUserInfo.userId != 0L && userInfo.userId != currentUserInfo.userId.toInt()) {
                    findNavController().navigate(
                        ParticipantListFragmentDirections.actionParticipantListFragmentToParticipantProfileFragment(
                            participant = userInfo,
                            recruitId = args.recruitId
                        )
                    )
                }
            }
        })
    }

    private fun setCheckMenuClickListener() {
        binding.btnParticipantListCheckMenu.setOnDebounceClickListener {
            findNavController().navigate(
                ParticipantListFragmentDirections.actionParticipantListFragmentToParticipantsOrderListFragment(
                    recruitId = args.recruitId
                )
            )
        }
    }
}