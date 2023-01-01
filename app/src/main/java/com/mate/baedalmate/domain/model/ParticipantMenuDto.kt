package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class ParticipantMenuDto (
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("userOrderTotal")
    val userOrderTotal: Int
)