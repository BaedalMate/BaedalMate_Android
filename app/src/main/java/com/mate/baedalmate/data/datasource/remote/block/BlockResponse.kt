package com.mate.baedalmate.data.datasource.remote.block

import com.google.gson.annotations.SerializedName

data class BlockResponse (
    @SerializedName("result")
    val result: String
)

data class BlockedUserListDto (
    @SerializedName("blockList")
    val blockList: List<BlockedUserDto>
)

data class BlockedUserDto (
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("profileImage")
    val profileImage: String
)