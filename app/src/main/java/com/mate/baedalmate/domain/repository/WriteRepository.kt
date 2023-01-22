package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitResponse
import com.mate.baedalmate.data.datasource.remote.write.ResultSuccessWithIdResponseDto
import com.mate.baedalmate.data.datasource.remote.write.UpdateRecruitDto
import com.mate.baedalmate.domain.model.ApiResult

interface WriteRepository {
    suspend fun requestUploadPost(body: CreateRecruitRequest): ApiResult<CreateRecruitResponse>
    suspend fun requestModifyPost(recruitId: Int, body: UpdateRecruitDto): ApiResult<ResultSuccessWithIdResponseDto>
}