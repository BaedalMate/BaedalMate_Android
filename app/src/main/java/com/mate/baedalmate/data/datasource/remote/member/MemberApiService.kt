package com.mate.baedalmate.data.datasource.remote.member

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MemberApiService {
    @POST("/login/oauth2/kakao")
    suspend fun requestLoginKakao(@Body data: MemberOAuthRequest): Response<MemberOAuthResponse>
}