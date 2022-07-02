package com.mate.baedalmate.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mate.baedalmate.common.extension.readValue
import com.mate.baedalmate.common.extension.storeValue
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.domain.repository.TokenPreferencesRepository
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

    private companion object {
        val kakaoAccessTokenKey = stringPreferencesKey("kakaoAccessToken")
        val accessTokenKey = stringPreferencesKey("accessToken")
        val refreshTokenKey = stringPreferencesKey("refreshToken")
    }
}