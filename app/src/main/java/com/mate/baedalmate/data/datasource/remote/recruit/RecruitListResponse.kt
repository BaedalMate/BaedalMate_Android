package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.RecruitList

data class RecruitListResponse (
    @SerializedName("RecruitList")
    val RecruitList: RecruitList
)