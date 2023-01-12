package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitList
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.ResultSuccessResponseDto
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import okhttp3.MultipartBody

interface MemberRepository {
    suspend fun requestLoginKakao(body: MemberOAuthRequest): ApiResult<MemberOAuthResponse>
    suspend fun requestGetUserInfo(): ApiResult<UserInfoResponse>
    suspend fun requestPutUserDormitory(newDormitory: Dormitory): ApiResult<Void>
    suspend fun requestPutChangeMyProfile(updateUserInfo: UpdateUserDto): ApiResult<UserInfoResponse>
    suspend fun requestPutChangeMyProfilePhoto(
        uploadfile: MultipartBody.Part?
    ): ApiResult<Void>
    suspend fun requestGetHistoryPostCreated(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<HistoryRecruitList>
    suspend fun requestGetHistoryPostParticipated(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<HistoryRecruitList>
    suspend fun requestGetResignUser(): ApiResult<ResultSuccessResponseDto>
}