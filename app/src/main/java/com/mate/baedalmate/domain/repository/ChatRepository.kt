package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.chat.ChatRoomList
import retrofit2.Response

interface ChatRepository {
    suspend fun requestGetChatRoomList(): Response<ChatRoomList>
}