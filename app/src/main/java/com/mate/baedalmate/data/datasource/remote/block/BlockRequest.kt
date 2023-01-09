package com.mate.baedalmate.data.datasource.remote.block

import com.google.gson.annotations.SerializedName

data class BlockRequestDto (
    @SerializedName("userId")
    val userId: Int
)