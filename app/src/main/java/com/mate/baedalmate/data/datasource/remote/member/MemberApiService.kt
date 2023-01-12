package com.mate.baedalmate.data.datasource.remote.member

import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface MemberApiService {
    @POST("/login/oauth2/kakao")
    suspend fun requestLoginKakao(@Body data: MemberOAuthRequest): Response<MemberOAuthResponse>

    @GET("/api/v1/user")
    suspend fun requestGetUserInfo(): Response<UserInfoResponse>

    @PUT("/api/v1/user")
    suspend fun requestPutUserDormitory(@Query("dormitory") dormitory: Dormitory): Response<Void>

    @PATCH("/api/v1/user")
    suspend fun requestPutChangeMyProfile(@Body updateUserInfo: UpdateUserDto): Response<UserInfoResponse>

    @Multipart
    @PUT("/api/v1/user/image")
    suspend fun requestPutChangeMyProfilePhoto(@Part uploadfile: MultipartBody.Part?): Response<Void>

    @GET("/api/v1/user/hosted-recruit")
    suspend fun requestGetHistoryPostCreated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<HistoryRecruitList>

    @GET("/api/v1/user/participated-recruit")
    suspend fun requestGetHistoryPostParticipated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<HistoryRecruitList>

    @GET("/api/v1/user/deactivate")
    suspend fun requestGetResignUser(): Response<ResultSuccessResponseDto>
}