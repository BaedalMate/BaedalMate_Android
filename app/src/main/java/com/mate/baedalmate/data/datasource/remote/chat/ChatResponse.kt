package com.mate.baedalmate.data.datasource.remote.chat

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.MessageInfo
import com.mate.baedalmate.domain.model.RecruitFinishCriteria

data class ChatRoomList(
    @SerializedName("rooms")
    val rooms: List<ChatRoomInfo>
)

data class ChatRoomInfo(
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("lastMessage")
    val lastMessage: MessageInfo,
    @SerializedName("title")
    val title: String
)

data class ChatRoomDetail(
    @SerializedName("chatRoomId")
    val chatRoomId: String,
    @SerializedName("recruit")
    val recruit: JsonObject,
    @SerializedName("messages")
    val messages: List<MessageInfo>,
    @SerializedName("reviewed")
    val reviewed: Boolean
)

data class ChatRoomRecruitDetailDto(
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("recruitImage")
    val recruitImage: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("cancel")
    val cancel: Boolean,
    @SerializedName("fail")
    val fail: Boolean
)