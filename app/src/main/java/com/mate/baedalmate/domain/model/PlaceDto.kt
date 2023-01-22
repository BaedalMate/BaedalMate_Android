package com.mate.baedalmate.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
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
): Parcelable
