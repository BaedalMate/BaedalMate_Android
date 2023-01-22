package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitResponse
import com.mate.baedalmate.data.datasource.remote.write.ResultSuccessWithIdResponseDto
import com.mate.baedalmate.data.datasource.remote.write.UpdateRecruitDto
import com.mate.baedalmate.data.datasource.remote.write.WriteApiService
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.WriteRepository
import javax.inject.Inject

class WriteRepositoryImpl @Inject constructor(private val writeApiService: WriteApiService) :
    WriteRepository {
    override suspend fun requestUploadPost(body: CreateRecruitRequest): ApiResult<CreateRecruitResponse> =
        setExceptionHandling { writeApiService.requestWritePost(data = body) }

    override suspend fun requestModifyPost(
        recruitId: Int,
        body: UpdateRecruitDto
    ): ApiResult<ResultSuccessWithIdResponseDto> =
        setExceptionHandling { writeApiService.requestModifyPost(id = recruitId, data = body) }
}