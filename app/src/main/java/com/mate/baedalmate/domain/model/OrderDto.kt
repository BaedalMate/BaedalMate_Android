package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class OrderDto (
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("recruitId")
    val recruitId: Int
)