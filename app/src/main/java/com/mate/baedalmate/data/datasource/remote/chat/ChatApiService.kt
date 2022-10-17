package com.mate.baedalmate.data.datasource.remote.chat

import retrofit2.Response
import retrofit2.http.GET

interface ChatApiService {
    @GET("/api/v1/rooms")
    suspend fun requestGetChatRoomList(): Response<ChatRoomList>
}