package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.chat.ChatApiService
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.MyMenuDto
import com.mate.baedalmate.domain.model.OrderDto
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatApiService: ChatApiService) :
    ChatRepository {
    override suspend fun requestGetChatRoomList(): ApiResult<ChatRoomList> =
        setExceptionHandling { chatApiService.requestGetChatRoomList() }

    override suspend fun requestGetChatRoomDetail(roomId: Int): ApiResult<ChatRoomDetail> =
        setExceptionHandling { chatApiService.requestGetChatRoomDetail(roomId = roomId) }

    override suspend fun requestGetChatParticipants(recruitId: Int): ApiResult<ParticipantsDto> =
        setExceptionHandling { chatApiService.requestGetChatParticipants(id = recruitId) }

    override suspend fun requestGetAllMenuList(recruitId: Int): ApiResult<ParticipantsMenuDto> =
        setExceptionHandling { chatApiService.requestGetAllMenuList(id = recruitId) }

    override suspend fun requestGetMyMenuList(recruitId: Int): ApiResult<MyMenuDto> =
        setExceptionHandling { chatApiService.requestGetMyMenuList(id = recruitId) }

    override suspend fun requestPutChangeMyMenuList(data: OrderDto): ApiResult<Void> =
        setExceptionHandling { chatApiService.requestPutChangeMyMenuList(data = data) }
}