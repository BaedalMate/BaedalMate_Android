package com.mate.baedalmate.data.datasource.remote.write

import com.google.gson.annotations.SerializedName

data class CreateRecruitResponse(
    @SerializedName("id")
    val id: Int
)