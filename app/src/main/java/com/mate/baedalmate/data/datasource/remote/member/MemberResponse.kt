package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberOAuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)
