package com.mate.baedalmate.data.datasource.remote.block

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockApiService {
    @POST("/api/v1/block")
    suspend fun requestPostBlockUser(@Body data: BlockRequestDto): Response<BlockResponse>
    @POST("/api/v1/unblock")
    suspend fun requestPostUnblockUser(@Body data: BlockRequestDto): Response<BlockResponse>
}