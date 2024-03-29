package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitDto (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("currentPeople")
    val currentPeople: Int,
    @SerializedName("currentPrice")
    val currentPrice: Int,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("place")
    val place: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("userScore")
    val userScore: Float,
    @SerializedName("active")
    val active: Boolean
)