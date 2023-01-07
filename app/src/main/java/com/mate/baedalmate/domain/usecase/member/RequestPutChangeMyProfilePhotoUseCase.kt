package com.mate.baedalmate.domain.usecase.member

import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.repository.MemberRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPutChangeMyProfilePhotoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(newImageFile: File): ApiResult<Void> {
        val requestProfileImage: MultipartBody.Part? = if (newImageFile != null)
            MultipartBody.Part.createFormData(
                "uploadfile",
                newImageFile.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), newImageFile)
            )
        else null
        return memberRepository.requestPutChangeMyProfilePhoto(
            uploadfile = requestProfileImage
        )
    }
}