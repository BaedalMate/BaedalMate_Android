package com.mate.baedalmate.data.datasource.remote.chat

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.MessageInfo
import com.mate.baedalmate.domain.model.RecruitInfo

data class ChatRoomList(
    @SerializedName("rooms")
    val rooms: List<ChatRoomInfo>
)

data class ChatRoomInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("lastMessage")
    val lastMessage: MessageInfo
)

data class ChatRoomDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("messages")
    val messages: List<MessageInfo>,
    @SerializedName("recruit")
    val recruit: RecruitInfo
)