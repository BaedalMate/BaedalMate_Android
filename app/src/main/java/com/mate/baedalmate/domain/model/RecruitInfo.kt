package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitInfo(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("recruitImage")
    val recruitImage: String,
    @SerializedName("title")
    val title: String
)
