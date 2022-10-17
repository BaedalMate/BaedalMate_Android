package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class MessageInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("sendDate")
    val sendDate: String,
    @SerializedName("sender")
    val sender: String
)