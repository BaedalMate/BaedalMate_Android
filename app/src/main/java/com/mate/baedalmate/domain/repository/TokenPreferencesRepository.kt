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
    suspend fun setFcmToken(fcmToken: String)
    suspend fun getFcmToken(): String
    suspend fun setNotificationPermitAll(isPermit: Boolean)
    suspend fun getNotificationPermitAll(): Boolean
    suspend fun setNotificationPermitNewMessage(isPermit: Boolean)
    suspend fun getNotificationPermitNewMessage(): Boolean
    suspend fun setNotificationPermitRecruit(isPermit: Boolean)
    suspend fun getNotificationPermitRecruit(): Boolean
    suspend fun setNotificationPermitNotice(isPermit: Boolean)
    suspend fun getNotificationPermitNotice(): Boolean
    suspend fun clearAllInfo()
}