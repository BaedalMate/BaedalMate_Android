package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitResponse
import com.mate.baedalmate.domain.model.ApiResult
import retrofit2.Response

interface WriteRepository {
    suspend fun requestUploadPost(body: CreateRecruitRequest): ApiResult<CreateRecruitResponse>
}