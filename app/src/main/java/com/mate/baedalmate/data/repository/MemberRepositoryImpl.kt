package com.mate.baedalmate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mate.baedalmate.data.datasource.PostHistoryPagingSource
import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitResponseDto
import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.ResultSuccessResponseDto
import com.mate.baedalmate.data.datasource.remote.member.UpdateDormitoryDto
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(private val memberApiService: MemberApiService) :
    MemberRepository {
    override suspend fun requestLoginKakao(data: MemberOAuthRequest): ApiResult<MemberOAuthResponse> =
        setExceptionHandling { memberApiService.requestLoginKakao(data) }

    override suspend fun requestGetUserInfo(): ApiResult<UserInfoResponse> =
        setExceptionHandling { memberApiService.requestGetUserInfo() }

    override suspend fun requestPutUserDormitory(newDormitory: Dormitory): ApiResult<ResultSuccessResponseDto> =
        setExceptionHandling {
            memberApiService.requestPutUserDormitory(
                dormitory = UpdateDormitoryDto(
                    newDormitory
                )
            )
        }

    override suspend fun requestPostChangeMyProfile(
        isChangingDefaultImage: Boolean,
        newNickname: String,
        uploadfile: MultipartBody.Part?
    ): ApiResult<UserInfoResponse> =
        setExceptionHandling { memberApiService.requestPostChangeMyProfile(default_image = isChangingDefaultImage,nickname = newNickname, uploadfile = uploadfile) }

    override suspend fun requestGetHistoryPostCreated(sort: String): Flow<PagingData<HistoryRecruitResponseDto>> =
        Pager(
            config = PagingConfig(pageSize = 6, maxSize = 30, enablePlaceholders = false),
            pagingSourceFactory = {
                PostHistoryPagingSource(
                    memberApiService,
                    isHostingPosts = true,
                    sort
                )
            }
        ).flow

    override suspend fun requestGetHistoryPostParticipated(sort: String): Flow<PagingData<HistoryRecruitResponseDto>> =
        Pager(
            config = PagingConfig(pageSize = 6, maxSize = 30, enablePlaceholders = false),
            pagingSourceFactory = {
                PostHistoryPagingSource(
                    memberApiService,
                    isHostingPosts = false,
                    sort
                )
            }
        ).flow

    override suspend fun requestPostLogout(): ApiResult<ResultSuccessResponseDto> =
        setExceptionHandling { memberApiService.requestPostLogout() }

    override suspend fun requestDeleteResignUser(): ApiResult<ResultSuccessResponseDto> =
        setExceptionHandling { memberApiService.requestDeleteResignUser() }
}