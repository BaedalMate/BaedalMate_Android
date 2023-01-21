package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.data.datasource.remote.member.UserInfoResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.repository.MemberRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPutChangeMyProfileUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(
        isChangingDefaultImage: Boolean,
        newNickname: String,
        newImageFile: File?
    ): ApiResult<UserInfoResponse> {
        val requestProfileImage: MultipartBody.Part? = if (newImageFile != null)
            MultipartBody.Part.createFormData(
                "uploadfile",
                newImageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), newImageFile)
            ) else MultipartBody.Part.createFormData("uploadfile","");
        // null로 보내면 request가 되지 않는 문제에 따라 빈 파일을 보내는 것으로 처리
        return memberRepository.requestPostChangeMyProfile(
            isChangingDefaultImage = isChangingDefaultImage,
            newNickname = newNickname,
            uploadfile = requestProfileImage
        )
    }
}