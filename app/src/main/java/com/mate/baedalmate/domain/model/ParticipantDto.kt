package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class ParticipantDto(
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("userId")
    val userId: Int
)