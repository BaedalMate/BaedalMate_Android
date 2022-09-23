package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName

data class RecruitDetail (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("deliveryFee")
    val deliveryFee: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("participate")
    val participate: Boolean,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("thumbnailImage")
    val thumbnailImage: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("userScore")
    val userScore: Float,
    @SerializedName("username")
    val username: String,
)