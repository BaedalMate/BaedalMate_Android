package com.mate.baedalmate.domain.usecase.write

import com.mate.baedalmate.data.datasource.remote.write.UpdateRecruitDto
import com.mate.baedalmate.domain.repository.WriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestModifyPostUseCase @Inject constructor(private val writeRepository: WriteRepository) {
    suspend operator fun invoke(recruitId: Int, body: UpdateRecruitDto) =
        writeRepository.requestModifyPost(recruitId = recruitId, body = body)
}