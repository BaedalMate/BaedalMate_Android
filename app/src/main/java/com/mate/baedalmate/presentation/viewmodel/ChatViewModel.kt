package com.mate.baedalmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mate.baedalmate.common.Event
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.ApiResult
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
        requestGetChatRoomListUseCase().let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _chatRoomList.postValue(it) }
                }
            }
        }
    }

    fun getChatRoomDetailLog(roomId: Int) = viewModelScope.launch {
        requestGetChatRoomDetailUseCase(roomId = roomId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _chatRoomLog.postValue(it) }
                }
            }
        }
    }

    fun getChatParticipants(recruitId: Int) = viewModelScope.launch {
        requestGetChatParticipantsUseCase(recruitId = recruitId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _chatParticipants.postValue(it) }
                }
            }
        }
    }

    fun getAllMenuList(recruitId: Int) = viewModelScope.launch {
        requestGetAllMenuListUseCase(recruitId = recruitId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _allMenuList.postValue(it) }
                }
            }
        }
    }

    fun getMyMenuList(recruitId: Int) = viewModelScope.launch {
        requestGetMyMenuListUseCase(recruitId = recruitId).let { ApiResponse ->
            when (ApiResponse.status) {
                ApiResult.Status.SUCCESS -> {
                    ApiResponse.data.let { _myMenuList.postValue(it) }
                }
            }
        }
    }

    fun putChangeMyMenuList(changedMenuList: List<MenuDto>, recruitId: Int) =
        viewModelScope.launch {
            requestPutChangeMyMenuListUseCase(
                OrderDto(
                    changedMenuList,
                    recruitId
                )
            ).let { ApiResponse ->
                when (ApiResponse.status) {
                    ApiResult.Status.SUCCESS -> {
                        _isChangeMyMenuListSuccess.postValue(Event(true))
                    }
                }
            }
        }
}