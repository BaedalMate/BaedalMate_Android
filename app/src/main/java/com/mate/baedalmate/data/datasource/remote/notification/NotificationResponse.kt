package com.mate.baedalmate.data.datasource.remote.notification

import com.google.gson.annotations.SerializedName

data class NotificationList (
    @SerializedName("notifications")
    val notifications: List<Notification>
)

data class Notification (
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("createDate")
    val createDate: String
)

data class FcmAllow (
    @SerializedName("allowChat")
    val allowChat: Boolean,
    @SerializedName("allowRecruit")
    val allowRecruit: Boolean,
)