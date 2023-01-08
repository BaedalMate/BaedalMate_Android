package com.mate.baedalmate.data.datasource.remote.member

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.RecruitFinishCriteria

data class MemberOAuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

data class UserInfoResponse(
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("score")
    val score: Float,
    @SerializedName("userId")
    val userId: Long,
)

data class HistoryRecruitList (
    @SerializedName("recruitList")
    val recruitList: List<HistoryRecruitResponseDto>
)

data class HistoryRecruitResponseDto (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("recruitId")
    val recruitId: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("criteria")
    val criteria: RecruitFinishCriteria,
    @SerializedName("title")
    val title: String,
    @SerializedName("cancel")
    val cancel: Boolean,
    @SerializedName("fail")
    val fail: Boolean
)