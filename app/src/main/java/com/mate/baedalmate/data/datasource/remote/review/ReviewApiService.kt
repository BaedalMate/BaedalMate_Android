package com.mate.baedalmate.data.datasource.remote.review

import com.mate.baedalmate.domain.model.ParticipantsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {
    @GET("/api/v1/review/{id}/target")
    suspend fun requestGetTargetReviewUserList(@Path("id") id: Int): Response<ParticipantsDto>

    @POST("/api/v1/review")
    suspend fun requestReviewUsers(@Body data: CreateReviewDto): Response<Void>
}