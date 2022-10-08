package com.mate.baedalmate.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShippingFeeDto(
    @SerializedName("lowerPrice")
    val lowerPrice: Int,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("upperPrice")
    val upperPrice: Int
) : Parcelable
