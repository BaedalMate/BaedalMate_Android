package com.mate.baedalmate.data.datasource.remote.write

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.DeliveryPlatform
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.PlaceDto
import com.mate.baedalmate.domain.model.RecruitFinishCriteria
import com.mate.baedalmate.domain.model.ShippingFeeDto
import com.mate.baedalmate.domain.model.TagDto

data class CreateRecruitRequest (
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("coupon")
    val coupon: Int,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("dormitory")
    val dormitory: Dormitory,
    @SerializedName("freeShipping")
    val freeShipping: Boolean,
    @SerializedName("menu")
    val menu: List<MenuDto>,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("place")
    val place: PlaceDto,
    @SerializedName("platform")
    val platform: DeliveryPlatform,
    @SerializedName("shippingFee")
    val shippingFee: List<ShippingFeeDto>,
    @SerializedName("tags")
    val tags: List<TagDto>,
    @SerializedName("title")
    val title: String
)

data class UpdateRecruitDto (
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("place")
    val place: PlaceDto,
    @SerializedName("dormitory")
    val dormitory: Dormitory,
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
    @SerializedName("tags")
    val tags: List<TagDto>,
    @SerializedName("freeShipping")
    val freeShipping: Boolean,
    @SerializedName("menu")
    val menu: List<MenuDto>,
)