package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class MyMenuDto (
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userOrderTotal")
    val userOrderTotal: Int
)