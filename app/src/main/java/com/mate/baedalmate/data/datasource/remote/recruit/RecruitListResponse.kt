package com.mate.baedalmate.data.datasource.remote.recruit

import com.google.gson.annotations.SerializedName
import com.mate.baedalmate.domain.model.RecruitDto
import com.mate.baedalmate.domain.model.TagDto

data class RecruitListResponse (
    @SerializedName("recruitList")
    val recruitList: List<RecruitDto>
)

data class MainRecruitList (
    @SerializedName("recruitList")
    val recruitList: List<MainRecruitDto>
)


data class TagRecruitList (
    @SerializedName("recruitList")
    val recruitList: List<TagRecruitDto>
)

data class MainRecruitDto (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("currentPeople")
    val currentPeople: Int,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("minPeople")
    val minPeople: Int,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("place")
    val place: String,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("userScore")
    val userScore: Float,
    @SerializedName("username")
    val username: String,
)

data class TagRecruitDto (
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("deadlineDate")
    val deadlineDate: String,
    @SerializedName("dormitory")
    val dormitory: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("place")
    val place: String,
    @SerializedName("shippingFee")
    val shippingFee: Int,
    @SerializedName("tags")
    val tags: List<TagDto>,
    @SerializedName("userScore")
    val userScore: Float,
    @SerializedName("username")
    val username: String,
)

data class CreateOrderResponse (
    @SerializedName("chatRoomId")
    val chatRoomId: Int,
    @SerializedName("id")
    val id: Int
)