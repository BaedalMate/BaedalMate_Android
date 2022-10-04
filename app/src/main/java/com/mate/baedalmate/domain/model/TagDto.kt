package com.mate.baedalmate.domain.model

import com.google.gson.annotations.SerializedName

data class TagDto (
    @SerializedName("tagname")
    val tagname: String
)