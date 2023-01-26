package com.mate.baedalmate.data.datasource.remote.search

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.RecruitDto

data class SearchRecruitList (
    @SerializedName("recruitList")
    val recruitList: List<RecruitDto>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("last")
    val last: Boolean
)