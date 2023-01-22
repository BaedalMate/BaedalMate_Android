package com.mate.baedalmate.data.datasource.remote.write

import com.google.gson.annotations.SerializedName

data class CreateRecruitResponse(
    @SerializedName("recruitId")
    val recruitId: Int
)

data class ResultSuccessWithIdResponseDto(
    @SerializedName("result")
    val result: String,
    @SerializedName("id")
    val id: String
)