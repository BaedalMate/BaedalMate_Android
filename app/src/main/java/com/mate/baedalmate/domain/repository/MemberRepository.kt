package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.Dormitory
import retrofit2.Response

interface MemberRepository {
    suspend fun requestLoginKakao(body: MemberOAuthRequest): Response<MemberOAuthResponse>
    suspend fun requestGetUserInfo(): Response<UserInfoResponse>
    suspend fun requestPutUserDormitory(newDormitory: Dormitory): Response<Void>
}