package com.mate.baedalmate.domain.usecase.write

import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.domain.repository.WriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestUploadPostUseCase @Inject constructor(private val writeRepository: WriteRepository) {
    suspend operator fun invoke(body: CreateRecruitRequest) =
        writeRepository.requestUploadPost(body = body)
}