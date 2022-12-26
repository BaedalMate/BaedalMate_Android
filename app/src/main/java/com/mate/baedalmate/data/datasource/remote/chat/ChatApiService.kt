package com.mate.baedalmate.data.datasource.remote.chat

import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.ParticipantsMenuDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApiService {
    @GET("/api/v1/rooms")
    suspend fun requestGetChatRoomList(): Response<ChatRoomList>

    @GET("/api/v1/room/{roomId}")
    suspend fun requestGetChatRoomDetail(@Path("roomId") roomId: Int): Response<ChatRoomDetail>

    @GET("/api/v1/recruit/{id}/participants")
    suspend fun requestGetChatParticipants(@Path("roomId") roomId: Int): Response<ParticipantsDto>

    @GET("/api/v1/recruit/{id}/menu")
    suspend fun requestGetAllMenuList(@Path("roomId") roomId: Int): Response<ParticipantsMenuDto>
}