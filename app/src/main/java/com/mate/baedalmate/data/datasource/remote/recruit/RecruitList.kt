package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName

data class RecruitList (
    @SerializedName("RecruitDto")
    val RecruitDto: List<RecruitDto>,
)