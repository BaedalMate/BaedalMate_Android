package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName

data class PlaceDto(
    @SerializedName("addressName")
    val addressName: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("roadAddressName")
    val roadAddressName: String,
    @SerializedName("x")
    val x: Float,
    @SerializedName("y")
    val y: Float,
)
