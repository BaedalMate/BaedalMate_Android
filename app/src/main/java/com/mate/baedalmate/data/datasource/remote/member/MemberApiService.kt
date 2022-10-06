package com.mate.baedalmate.data.datasource.remote.member

import com.mate.baedalmate.domain.model.Dormitory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface MemberApiService {
    @POST("/login/oauth2/kakao")
    suspend fun requestLoginKakao(@Body data: MemberOAuthRequest): Response<MemberOAuthResponse>
    @GET("/api/v1/user")
    suspend fun requestGetUserInfo(): Response<UserInfoResponse>
    @PUT("/api/v1/user")
    suspend fun requestPutUserDormitory(@Query("dormitory") dormitory: Dormitory): Response<Void>
}