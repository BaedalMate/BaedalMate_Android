package com.mate.baedalmate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mate.baedalmate.common.extension.readValue
import com.mate.baedalmate.common.extension.storeValue
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenPreferencesRepositoryImpl @Inject constructor(private val tokenDataStorePreferences: DataStore<Preferences>) :
    TokenPreferencesRepository {
    override suspend fun setKakaoAccessToken(kakaoAccessToken: String) {
        tokenDataStorePreferences.storeValue(kakaoAccessTokenKey, kakaoAccessToken)
    }

    override suspend fun getKakaoAccessToken(): String =
        tokenDataStorePreferences.readValue(kakaoAccessTokenKey) ?: ""

    override suspend fun setOAuthToken(memberOAuthResponse: MemberOAuthResponse) {
        tokenDataStorePreferences.storeValue(accessTokenKey, memberOAuthResponse.accessToken)
        tokenDataStorePreferences.storeValue(refreshTokenKey, memberOAuthResponse.refreshToken)
    }

    override suspend fun getOAuthToken(): MemberOAuthResponse {
        return MemberOAuthResponse(
            tokenDataStorePreferences.readValue(accessTokenKey) ?: "",
            tokenDataStorePreferences.readValue(refreshTokenKey) ?: ""
        )
    }

    override val isAccountValid: Flow<Boolean?>
        get() = tokenDataStorePreferences.data.catch { exception ->
            throw exception
        }.map { preferences ->
            preferences[userAccountValidKey]
        }

    override suspend fun setUserInfo(userInfo: UserInfoResponse) {
        tokenDataStorePreferences.storeValue(userIdKey, userInfo.userId)
        tokenDataStorePreferences.storeValue(userNicknameKey, userInfo.nickname)
        tokenDataStorePreferences.storeValue(userProfileImageKey, userInfo.profileImage)
        tokenDataStorePreferences.storeValue(userDormitoryKey, userInfo.dormitory)
        tokenDataStorePreferences.storeValue(userScoreKey, userInfo.score)
    }

    override suspend fun getUserInfo(): UserInfoResponse {
        return UserInfoResponse(
            userId = tokenDataStorePreferences.readValue(userIdKey) ?: 0L,
            nickname = tokenDataStorePreferences.readValue(userNicknameKey) ?: "",
            profileImage = tokenDataStorePreferences.readValue(userProfileImageKey) ?: "",
            dormitory = tokenDataStorePreferences.readValue(userDormitoryKey) ?: "",
            score = tokenDataStorePreferences.readValue(userScoreKey) ?: 0f
        )
    }

    override suspend fun setUserAccountIsValid(isValid: Boolean) {
        tokenDataStorePreferences.storeValue(userAccountValidKey, isValid)
    }

    override suspend fun getUserAccountIsValid(): Boolean {
        return tokenDataStorePreferences.readValue(userAccountValidKey) ?: false
    }

    override suspend fun clearAllInfo() {
        tokenDataStorePreferences.edit {
            it.clear()
        }
    }

    override suspend fun setFcmToken(fcmToken: String) {
        tokenDataStorePreferences.storeValue(fcmTokenKey, fcmToken)
    }

    override suspend fun getFcmToken(): String =
        tokenDataStorePreferences.readValue(fcmTokenKey) ?: ""

    override suspend fun setNotificationPermitAll(isPermit: Boolean) =
        tokenDataStorePreferences.storeValue(notificationPermitAllKey, isPermit)

    override suspend fun getNotificationPermitAll(): Boolean =
        tokenDataStorePreferences.readValue(notificationPermitAllKey) ?: false

    override suspend fun setNotificationPermitNewMessage(isPermit: Boolean) =
        tokenDataStorePreferences.storeValue(notificationPermitNewMessageKey, isPermit)

    override suspend fun getNotificationPermitNewMessage(): Boolean  =
        tokenDataStorePreferences.readValue(notificationPermitNewMessageKey) ?: false

    override suspend fun setNotificationPermitRecruit(isPermit: Boolean) =
        tokenDataStorePreferences.storeValue(notificationPermitRecruitKey, isPermit)

    override suspend fun getNotificationPermitRecruit(): Boolean  =
        tokenDataStorePreferences.readValue(notificationPermitRecruitKey) ?: false

    override suspend fun setNotificationPermitNotice(isPermit: Boolean) =
        tokenDataStorePreferences.storeValue(notificationPermitNoticeKey, isPermit)

    override suspend fun getNotificationPermitNotice(): Boolean =
        tokenDataStorePreferences.readValue(notificationPermitNoticeKey) ?: false

    private companion object {
        val kakaoAccessTokenKey = stringPreferencesKey("kakaoAccessToken")
        val accessTokenKey = stringPreferencesKey("accessToken")
        val refreshTokenKey = stringPreferencesKey("refreshToken")
        val fcmTokenKey = stringPreferencesKey("fcmToken")

        val userIdKey = longPreferencesKey("userId")
        val userNicknameKey = stringPreferencesKey("userNickname")
        val userProfileImageKey = stringPreferencesKey("userProfileImage")
        val userDormitoryKey = stringPreferencesKey("userDormitory")
        val userScoreKey = floatPreferencesKey("userScore")
        val userAccountValidKey = booleanPreferencesKey("userAccountValid")

        val notificationPermitAllKey = booleanPreferencesKey("notificationPermitAll")
        val notificationPermitNewMessageKey = booleanPreferencesKey("notificationPermitNewMessage")
        val notificationPermitRecruitKey = booleanPreferencesKey("notificationPermitRecruit")
        val notificationPermitNoticeKey = booleanPreferencesKey("notificationPermitNotice")
    }
}