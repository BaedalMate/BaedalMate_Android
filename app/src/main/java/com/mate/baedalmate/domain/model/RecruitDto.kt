package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitDto (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("currentPeople")
    val currentPeople: Int,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("place")
    val place: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("userScore")
    val userScore: Float
)