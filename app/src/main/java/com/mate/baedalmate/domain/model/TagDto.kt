package com.mate.baedalmate.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TagDto (
    @SerializedName("tagname")
    val tagname: String
): Parcelable