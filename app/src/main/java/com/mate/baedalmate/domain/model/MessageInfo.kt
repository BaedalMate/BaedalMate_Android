package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class MessageInfo(
    @SerializedName("messageId")
    val messageId: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("sendDate")
    val sendDate: String,
    @SerializedName("sender")
    val sender: String,
    @SerializedName("senderId")
    val senderId: Int,
    @SerializedName("senderImage")
    val senderImage: String?,
)

data class ReceiveMessageInfo(
    @SerializedName("senderId")
    val senderId: Int,
    @SerializedName("sender")
    val sender: String,
    @SerializedName("senderImage")
    val senderImage: String,
    @SerializedName("roomId")
    val roomId: Long,
    @SerializedName("message")
    val message: String
)