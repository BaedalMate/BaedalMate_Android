package com.mate.baedalmate.data.datasource.remote.block

import com.google.gson.annotations.SerializedName

data class BlockResponse (
    @SerializedName("result")
    val result: String
)