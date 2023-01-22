package com.mate.baedalmate.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import kotlinx.parcelize.Parcelize

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
    @SerializedName("participate")
    val participate: Boolean,
    @SerializedName("place")
    val place: PlaceDto,
    @SerializedName("platform")
    val platform: String,
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
    @SerializedName("userInfo")
    val userInfo: UserInfoResponse,
    @SerializedName("currentPrice")
    val currentPrice: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("cancel")
    val cancel: Boolean
)

@Parcelize
data class RecruitDetailForModify(
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("place")
    val place: PlaceDto,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("shippingFee")
    val shippingFee: List<ShippingFeeDto>,
    @SerializedName("coupon")
    val coupon: Int,
    @SerializedName("platform")
    val platform: DeliveryPlatform,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("freeShipping")
    val freeShipping: Boolean,
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("tags")
    val tags: List<TagDto>
): Parcelable