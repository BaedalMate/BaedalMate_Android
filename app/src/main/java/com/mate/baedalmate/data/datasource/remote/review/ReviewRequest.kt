package com.mate.baedalmate.data.datasource.remote.review

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.MenuDto
import com.mate.baedalmate.domain.model.UserDto

data class CreateReviewDto (
    @SerializedName("users")
    val users: List<UserDto>,
    @SerializedName("recruitId")
    val recruitId: Int
)