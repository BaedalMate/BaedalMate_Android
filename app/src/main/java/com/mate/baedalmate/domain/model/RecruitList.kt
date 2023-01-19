package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class RecruitList (
    @SerializedName("recruitList")
    val recruitList: List<RecruitDto>,
    @SerializedName("last")
    val last: Boolean
)