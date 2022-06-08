package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberOAuthRequest(
    @SerializedName("kakao_access_token")
    val kakao_access_token: String
)