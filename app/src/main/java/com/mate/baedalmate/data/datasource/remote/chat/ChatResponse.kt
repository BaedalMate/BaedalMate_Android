package com.mate.baedalmate.data.datasource.remote.chat

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.MessageInfo

data class ChatRoomList(
    @SerializedName("rooms")
    val rooms: List<ChatRoomInfo>
)

data class ChatRoomInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastMessage")
    val lastMessage: MessageInfo
)