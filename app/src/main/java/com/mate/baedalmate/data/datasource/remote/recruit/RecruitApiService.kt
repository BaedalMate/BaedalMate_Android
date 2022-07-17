package com.mate.baedalmate.data.datasource.remote.recruit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RecruitApiService {
    @GET("/api/v1/recruit/list")
    suspend fun requestRecruitList(
        @Query("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<RecruitListResponse>
}