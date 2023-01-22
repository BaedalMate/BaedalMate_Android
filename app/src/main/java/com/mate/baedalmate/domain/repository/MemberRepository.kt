package com.mate.baedalmate.domain.repository

import androidx.paging.PagingData
import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitResponseDto
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.ResultSuccessResponseDto
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface MemberRepository {
    suspend fun requestLoginKakao(body: MemberOAuthRequest): ApiResult<MemberOAuthResponse>
    suspend fun requestGetUserInfo(): ApiResult<UserInfoResponse>
    suspend fun requestPutUserDormitory(newDormitory: Dormitory): ApiResult<ResultSuccessResponseDto>
    suspend fun requestPostChangeMyProfile(isChangingDefaultImage: Boolean, newNickname: String, uploadfile: MultipartBody.Part?): ApiResult<UserInfoResponse>
    suspend fun requestGetHistoryPostCreated(sort: String): Flow<PagingData<HistoryRecruitResponseDto>>
    suspend fun requestGetHistoryPostParticipated(sort: String): Flow<PagingData<HistoryRecruitResponseDto>>
    suspend fun requestPostLogout(): ApiResult<ResultSuccessResponseDto>
    suspend fun requestDeleteResignUser(): ApiResult<ResultSuccessResponseDto>
}