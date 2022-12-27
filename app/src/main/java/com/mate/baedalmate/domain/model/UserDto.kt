package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("score")
    var score: Float,
    @SerializedName("userId")
    val userId: Int
)
