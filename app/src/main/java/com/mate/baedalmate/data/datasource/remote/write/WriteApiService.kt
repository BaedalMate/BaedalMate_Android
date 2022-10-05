package com.mate.baedalmate.data.datasource.remote.write

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WriteApiService {
    @POST("/api/v1/recruit/new")
    suspend fun requestWritePost(@Body data: CreateRecruitRequest): Response<CreateRecruitResponse>
}