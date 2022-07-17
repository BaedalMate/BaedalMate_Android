package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName

data class RecruitListResponse (
    @SerializedName("RecruitList")
    val RecruitList: RecruitList
)