package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.member.HistoryRecruitList
import com.mate.baedalmate.data.datasource.remote.member.MemberApiService
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthRequest
import com.mate.baedalmate.data.datasource.remote.member.MemberOAuthResponse
import com.mate.baedalmate.data.datasource.remote.member.ResultSuccessResponseDto
import com.mate.baedalmate.data.datasource.remote.member.UpdateDormitoryDto
import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.Dormitory
import com.mate.baedalmate.domain.model.UpdateUserDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.MemberRepository
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

    override suspend fun requestGetHistoryPostCreated(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<HistoryRecruitList> =
        setExceptionHandling {
            memberApiService.requestGetHistoryPostCreated(
                page = page,
                size = size,
                sort = sort
            )
        }

    override suspend fun requestGetHistoryPostParticipated(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<HistoryRecruitList>  =
        setExceptionHandling {
            memberApiService.requestGetHistoryPostParticipated(
                page = page,
                size = size,
                sort = sort
            )
        }

    override suspend fun requestPostLogout(): ApiResult<ResultSuccessResponseDto> =
        setExceptionHandling { memberApiService.requestPostLogout() }

    override suspend fun requestGetResignUser(): ApiResult<ResultSuccessResponseDto> =
        setExceptionHandling { memberApiService.requestGetResignUser() }
}