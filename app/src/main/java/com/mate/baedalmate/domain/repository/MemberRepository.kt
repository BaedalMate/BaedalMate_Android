package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import okhttp3.MultipartBody
import retrofit2.Response

interface MemberRepository {
    suspend fun requestLoginKakao(body: MemberOAuthRequest): Response<MemberOAuthResponse>
    suspend fun requestGetUserInfo(): Response<UserInfoResponse>
    suspend fun requestPutUserDormitory(newDormitory: Dormitory): Response<Void>
    suspend fun requestPutChangeMyProfile(updateUserInfo: UpdateUserDto): Response<UserInfoResponse>
    suspend fun requestPutChangeMyProfilePhoto(
        uploadfile: MultipartBody.Part?
    ): Response<Void>
}