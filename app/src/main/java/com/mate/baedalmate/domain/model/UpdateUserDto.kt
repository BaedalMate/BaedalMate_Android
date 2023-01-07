package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class UpdateUserDto(
    @SerializedName("nickname")
    val nickname: String
)