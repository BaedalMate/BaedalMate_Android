package com.mate.baedalmate.data.datasource.remote.chat

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApiService {
    @GET("/api/v1/rooms")
    suspend fun requestGetChatRoomList(): Response<ChatRoomList>

    @GET("/api/v1/room/{roomId}")
    suspend fun requestGetChatRoomDetail(@Path("roomId") roomId: Int): Response<ChatRoomDetail>
}