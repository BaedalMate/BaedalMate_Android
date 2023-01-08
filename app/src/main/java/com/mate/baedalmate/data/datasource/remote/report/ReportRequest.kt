package com.mate.baedalmate.data.datasource.remote.report

import com.google.gson.annotations.SerializedName

data class UserReportRequestDto (
    @SerializedName("targetUserId")
    val targetUserId: Int,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("detail")
    val detail: String
)

data class RecruitReportRequestDto (
    @SerializedName("targetRecruitId")
    val targetRecruitId: Int,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("detail")
    val detail: String
)