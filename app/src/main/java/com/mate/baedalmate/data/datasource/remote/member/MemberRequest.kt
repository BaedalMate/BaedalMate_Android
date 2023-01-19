package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.Dormitory

data class MemberOAuthRequest(
    @SerializedName("kakaoAccessToken")
    val kakaoAccessToken: String
)

data class UpdateDormitoryDto(
    @SerializedName("dormitory")
    val dormitory: Dormitory
)