package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.MyMenuDto
import com.mate.baedalmate.domain.model.OrderDto
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import com.mate.baedalmate.domain.usecase.chat.RequestGetAllMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatParticipantsUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomDetailUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetChatRoomListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestGetMyMenuListUseCase
import com.mate.baedalmate.domain.usecase.chat.RequestPutChangeMyMenuListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val requestGetChatRoomListUseCase: RequestGetChatRoomListUseCase,
    private val requestGetChatRoomDetailUseCase: RequestGetChatRoomDetailUseCase,
    private val requestGetChatParticipantsUseCase: RequestGetChatParticipantsUseCase,
    private val requestGetAllMenuListUseCase: RequestGetAllMenuListUseCase,
    private val requestGetMyMenuListUseCase: RequestGetMyMenuListUseCase,
    private val requestPutChangeMyMenuListUseCase: RequestPutChangeMyMenuListUseCase
) : ViewModel() {
    private val _chatRoomList = MutableLiveData<ChatRoomList>()
    val chatRoomList: LiveData<ChatRoomList> get() = _chatRoomList

    private val _chatRoomLog = MutableLiveData<ChatRoomDetail>()
    val chatRoomLog: LiveData<ChatRoomDetail> get() = _chatRoomLog

    private val _chatParticipants = MutableLiveData<ParticipantsDto>()
    val chatParticipants: LiveData<ParticipantsDto> get() = _chatParticipants

    private val _allMenuList = MutableLiveData<ParticipantsMenuDto>()
    val allMenuList: LiveData<ParticipantsMenuDto> get() = _allMenuList

    private val _myMenuList = MutableLiveData<MyMenuDto>()
    val myMenuList: LiveData<MyMenuDto> get() = _myMenuList

    private val _isChangeMyMenuListSuccess = MutableLiveData<Event<Boolean>>()
    val isChangeMyMenuListSuccess: LiveData<Event<Boolean>> get() = _isChangeMyMenuListSuccess

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

    fun getChatParticipants(roomId: Int) = viewModelScope.launch {
        val response = requestGetChatParticipantsUseCase(roomId = roomId)
        if (response.isSuccessful) {
            _chatParticipants.postValue(response.body())
        } else {

        }
    }

    fun getAllMenuList(roomId: Int) = viewModelScope.launch {
        val response = requestGetAllMenuListUseCase(roomId = roomId)
        if (response.isSuccessful) {
            _allMenuList.postValue(response.body())
        }
    }

    fun getMyMenuList(id: Int) = viewModelScope.launch {
        val response = requestGetMyMenuListUseCase(id = id)
        if (response.isSuccessful) {
            _myMenuList.postValue(response.body())
        }
    }

    fun putChangeMyMenuList(changedMenuList: List<MenuDto>, recruitId: Int) = viewModelScope.launch {
        val response = requestPutChangeMyMenuListUseCase(OrderDto(changedMenuList, recruitId))
        if (response.isSuccessful) {
            _isChangeMyMenuListSuccess.postValue(Event(true))
        }
    }
}