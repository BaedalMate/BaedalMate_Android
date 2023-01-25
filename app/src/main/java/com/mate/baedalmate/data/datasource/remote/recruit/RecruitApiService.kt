package com.mate.baedalmate.data.datasource.remote.recruit

import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitDetailForModify
import com.mate.baedalmate.domain.model.RecruitList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecruitApiService {
    @GET("/api/v1/recruit/list")
    suspend fun requestRecruitList(
        @Query("categoryId") categoryId: Int? = null,
        @Query("except_close") except_close: Boolean,
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

    @GET("/api/v1/recruit/{id}/detail")
    suspend fun requestRecruitPostDetailForModify(
        @Path("id") id: Int
    ): Response<RecruitDetailForModify>

    @GET("/api/v1/recruit/cancel/{id}")
    suspend fun requestCancelRecruitPost(@Path("id") id: Int): Response<Void>

    @GET("/api/v1/recruit/close/{id}")
    suspend fun requestCloseRecruitPost(@Path("id") id: Int): Response<Void>

    @POST("/api/v1/order")
    suspend fun requestParticipateRecruitPost(@Body data: CreateOrderRequest): Response<CreateOrderResponse>

    @DELETE("/api/v1/order")
    suspend fun requestCancelParticipateRecruitPost(@Body data: DeleteOrderDto): Response<Void>
}