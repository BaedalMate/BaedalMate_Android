package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomDetail
import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.MyMenuDto
import com.mate.baedalmate.domain.model.OrderDto
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import retrofit2.Response

interface ChatRepository {
    suspend fun requestGetChatRoomList(): ApiResult<ChatRoomList>
    suspend fun requestGetChatRoomDetail(roomId: Int): ApiResult<ChatRoomDetail>
    suspend fun requestGetChatParticipants(recruitId: Int): ApiResult<ParticipantsDto>
    suspend fun requestGetAllMenuList(recruitId: Int): ApiResult<ParticipantsMenuDto>
    suspend fun requestGetMyMenuList(recruitId: Int): ApiResult<MyMenuDto>
    suspend fun requestPutChangeMyMenuList(data: OrderDto): ApiResult<Void>
}