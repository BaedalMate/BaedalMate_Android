package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.MenuDto

data class CreateOrderRequest (
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("recruitId")
    val recruitId: Int
)

data class DeleteOrderDto (
    @SerializedName("recruitId")
    val recruitId: Int
)