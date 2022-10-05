package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitRequest
import com.mate.baedalmate.data.datasource.remote.write.CreateRecruitResponse
import com.mate.baedalmate.data.datasource.remote.write.WriteApiService
import com.mate.baedalmate.domain.repository.WriteRepository
import retrofit2.Response
import javax.inject.Inject

class WriteRepositoryImpl @Inject constructor(private val writeApiService: WriteApiService) :
    WriteRepository {
    override suspend fun requestUploadPost(body: CreateRecruitRequest): Response<CreateRecruitResponse> =
        writeApiService.requestWritePost(data = body)
}