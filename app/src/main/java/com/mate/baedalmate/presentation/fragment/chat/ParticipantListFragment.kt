package com.mate.baedalmate.presentation.fragment.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ParticipantListFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentParticipantListBinding>()
    private val memberViewModel by activityViewModels<MemberViewModel>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val args by navArgs<ParticipantListFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var participantListAdapter: ParticipantListAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var currentUserInfo: UserInfoResponse ?= null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return initBottomSheetDialog()
    }

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
        getChatParticipants()
        setParticipantListAdapter()
        setCheckMenuClickListener()
    }

    private fun initBottomSheetDialog(): BottomSheetDialog {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogRadius)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.behavior.skipCollapsed = true // Dialog가 길어지는 경우 Half_expand되는 경우 방지
        return bottomSheetDialog
    }

    private fun getUserInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentUserInfo = memberViewModel.getUserInfo()
            }
        }
    }

    private fun getChatParticipants() {
        chatViewModel.getChatParticipants(recruitId = args.recruitId)
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
                currentUserInfo?.let { currentUser ->
                    if (currentUser.userId != 0L && userInfo.userId != currentUser.userId.toInt()) {
                        findNavController().navigate(
                            ParticipantListFragmentDirections.actionParticipantListFragmentToParticipantProfileFragment(
                                participant = userInfo,
                                recruitId = args.recruitId
                            )
                        )
                    }
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