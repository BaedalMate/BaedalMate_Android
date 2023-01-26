package com.mate.baedalmate.data.datasource.remote.write

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WriteApiService {
    @POST("/api/v1/recruit/new")
    suspend fun requestWritePost(@Body data: CreateRecruitRequest): Response<CreateRecruitResponse>
    @PUT("/api/v1/recruit/{id}")
    suspend fun requestModifyPost(@Path("id") id: Int, @Body data: UpdateRecruitDto): Response<ResultSuccessWithIdResponseDto>
}