package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse

interface TokenPreferencesRepository {
    suspend fun setKakaoAccessToken(kakaoAccessToken: String)
    suspend fun getKakaoAccessToken(): String
    suspend fun setOAuthToken(memberOAuthResponse: MemberOAuthResponse)
    suspend fun getOAuthToken(): MemberOAuthResponse
}