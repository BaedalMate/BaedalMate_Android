package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName

data class MemberOAuthResponse(
    @SerializedName("refresh_token")
    val refresh_token: String,
    @SerializedName("access_token")
    val access_token: String
)
