package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import kotlinx.coroutines.flow.Flow

interface TokenPreferencesRepository {
    val isAccountValid: Flow<Boolean?>
    suspend fun setKakaoAccessToken(kakaoAccessToken: String)
    suspend fun getKakaoAccessToken(): String
    suspend fun setOAuthToken(memberOAuthResponse: MemberOAuthResponse)
    suspend fun getOAuthToken(): MemberOAuthResponse
    suspend fun setUserInfo(userInfo: UserInfoResponse)
    suspend fun getUserInfo(): UserInfoResponse
    suspend fun setUserAccountIsValid(isValid: Boolean)
    suspend fun getUserAccountIsValid(): Boolean
    suspend fun clearAllInfo()
}