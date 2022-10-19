package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitDetail(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("coupon")
    val coupon: Int,
    @SerializedName("currentPeople")
    val currentPeople: Int,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("host")
    val host: Boolean,
    @SerializedName("image")
    val image: String,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("place")
    val place: PlaceDto,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("score")
    val score: Float,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("shippingFeeDetail")
    val shippingFeeDetail: List<ShippingFeeDto>,
    @SerializedName("title")
    val title: String,
    @SerializedName("username")
    val username: String,
)