package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import retrofit2.Response

interface ChatRepository {
    suspend fun requestGetChatRoomList(): Response<ChatRoomList>
    suspend fun requestGetChatRoomDetail(roomId: Int): Response<ChatRoomDetail>
    suspend fun requestGetChatParticipants(roomId: Int): Response<ParticipantsDto>
    suspend fun requestGetAllMenuList(roomId: Int): Response<ParticipantsMenuDto>
}