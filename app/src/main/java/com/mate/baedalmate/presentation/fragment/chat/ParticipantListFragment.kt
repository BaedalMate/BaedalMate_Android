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
import com.mate.baedalmate.databinding.FragmentParticipantListBinding
import com.mate.baedalmate.domain.model.ParticipantDto
import com.mate.baedalmate.presentation.adapter.chat.ParticipantListAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParticipantListFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentParticipantListBinding>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private val args by navArgs<ParticipantListFragmentArgs>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var participantListAdapter: ParticipantListAdapter

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
        getChatParticipants()
        setParticipantListAdapter()
        setCheckMenuClickListener()
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
                findNavController().navigate(
                    ParticipantListFragmentDirections.actionParticipantListFragmentToParticipantProfileFragment(
                        participant = userInfo
                    )
                )
                // TODO 본인 프로필을 눌러도 아무 동작이 없도록 필터링해야함
            }
        })
    }

    private fun setCheckMenuClickListener() {
        binding.btnParticipantListCheckMenu.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_participantListFragment_to_participantsOrderListFragment)
        }
    }
}