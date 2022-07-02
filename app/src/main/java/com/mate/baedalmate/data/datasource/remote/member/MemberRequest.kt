package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberOAuthRequest(
    @SerializedName("kakaoAccessToken")
    val kakaoAccessToken: String
)