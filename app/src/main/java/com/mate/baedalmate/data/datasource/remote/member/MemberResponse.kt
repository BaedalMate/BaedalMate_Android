package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberOAuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

data class UserInfoResponse(
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("score")
    val score: Float,
)