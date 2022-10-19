package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomDetailUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val requestGetChatRoomListUseCase: RequestGetChatRoomListUseCase,
    private val requestGetChatRoomDetailUseCase: RequestGetChatRoomDetailUseCase
) : ViewModel() {
    private val _chatRoomList = MutableLiveData<ChatRoomList>()
    val chatRoomList: LiveData<ChatRoomList> get() = _chatRoomList

    private val _chatRoomLog = MutableLiveData<ChatRoomDetail>()
    val chatRoomLog: LiveData<ChatRoomDetail> get() = _chatRoomLog

    fun getChatRoomList() = viewModelScope.launch {
        val response = requestGetChatRoomListUseCase()
        if (response.isSuccessful) {
            _chatRoomList.postValue(response.body())
        } else {

        }
    }

    fun getChatRoomDetailLog(roomId: Int) = viewModelScope.launch {
        val response = requestGetChatRoomDetailUseCase(roomId = roomId)
        if (response.isSuccessful) {
            _chatRoomLog.postValue(response.body())
        } else {

        }
    }
}