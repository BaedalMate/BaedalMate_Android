package com.mate.baedalmate.data.datasource.remote.recruit

import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitApiService {
    @GET("/api/v1/recruit/list")
    suspend fun requestRecruitList(
        @Query("categoryId") categoryId: Int? = null,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<RecruitList>

    @GET("/api/v1/recruit/main/list")
    suspend fun requestRecruitMainList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<MainRecruitList>

    @GET("/api/v1/recruit/tag/list")
    suspend fun requestRecruitTagList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): Response<TagRecruitList>

    @GET("/api/v1/recruit/{id}")
    suspend fun requestRecruitPost(
        @Path("id") id: Int
    ): Response<RecruitDetail>

    @POST("/api/v1/order")
    suspend fun requestParticipateRecruitPost(@Body data: CreateOrderRequest): Response<CreateOrderResponse>
}