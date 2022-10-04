package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitList (
    @SerializedName("RecruitDto")
    val RecruitDto: List<RecruitDto>,
)