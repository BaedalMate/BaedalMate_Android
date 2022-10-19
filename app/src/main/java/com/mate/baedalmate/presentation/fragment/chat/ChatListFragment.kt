package com.mate.baedalmate.presentation.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.mate.baedalmate.common.autoCleared
import com.mate.baedalmate.databinding.FragmentChatListBinding
import com.mate.baedalmate.presentation.adapter.chat.ChatRoomListAdapter
import com.mate.baedalmate.presentation.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var binding by autoCleared<FragmentChatListBinding>()
    private val chatViewModel by activityViewModels<ChatViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var chatRoomListAdapter: ChatRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getChatRoomListData()
        initChatRoomList()
    }

    private fun getChatRoomListData() {
        chatViewModel.getChatRoomList()
    }

    private fun initChatRoomList() {
        chatRoomListAdapter = ChatRoomListAdapter(requestManager = glideRequestManager)
        binding.rvChatListContents.adapter = chatRoomListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatRoomList.observe(viewLifecycleOwner) { roomList ->
                    chatRoomListAdapter.submitList(roomList.rooms.toMutableList())
                }
            }
        }
    }
}