package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class MenuDto (
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("quantity")
    val quantity: Int,
)