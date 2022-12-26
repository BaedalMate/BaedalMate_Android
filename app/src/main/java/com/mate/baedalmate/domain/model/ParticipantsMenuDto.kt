package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class ParticipantsMenuDto(
    @SerializedName("allOrderTotal")
    val allOrderTotal: Int,
    @SerializedName("coupon")
    val coupon: Int,
    @SerializedName("myOrderPrice")
    val myOrderPrice: Int,
    @SerializedName("myPaymentPrice")
    val myPaymentPrice: Int,
    @SerializedName("number")
    val number: Int,
    @SerializedName("participants")
    val participants: List<ParticipantMenuDto>,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("shippingFeePerParticipant")
    val shippingFeePerParticipant: Int
)
