package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.chat.ChatApiService
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.MyMenuDto
import com.mate.baedalmate.domain.model.OrderDto
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import com.mate.baedalmate.domain.repository.ChatRepository
import retrofit2.Response
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatApiService: ChatApiService) :
    ChatRepository {
    override suspend fun requestGetChatRoomList(): Response<ChatRoomList> =
        chatApiService.requestGetChatRoomList()

    override suspend fun requestGetChatRoomDetail(roomId: Int): Response<ChatRoomDetail> =
        chatApiService.requestGetChatRoomDetail(roomId = roomId)

    override suspend fun requestGetChatParticipants(id: Int): Response<ParticipantsDto> =
        chatApiService.requestGetChatParticipants(id = id)

    override suspend fun requestGetAllMenuList(id: Int): Response<ParticipantsMenuDto> =
        chatApiService.requestGetAllMenuList(id = id)

    override suspend fun requestGetMyMenuList(id: Int): Response<MyMenuDto> =
        chatApiService.requestGetMyMenuList(id = id)

    override suspend fun requestPutChangeMyMenuList(data: OrderDto): Response<Void> =
        chatApiService.requestPutChangeMyMenuList(data = data)
}