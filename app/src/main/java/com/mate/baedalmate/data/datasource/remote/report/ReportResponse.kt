package com.mate.baedalmate.data.datasource.remote.report

import com.google.gson.annotations.SerializedName

data class ReportResponse (
    @SerializedName("result")
    val result: String
)