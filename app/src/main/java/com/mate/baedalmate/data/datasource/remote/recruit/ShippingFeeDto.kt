package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName

data class ShippingFeeDto(
    @SerializedName("lowerPrice")
    val lowerPrice: Int,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("upperPrice")
    val upperPrice: Int
)