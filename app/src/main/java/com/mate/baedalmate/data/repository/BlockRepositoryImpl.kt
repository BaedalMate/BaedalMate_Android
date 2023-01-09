package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.block.BlockApiService
import com.mate.baedalmate.data.datasource.remote.block.BlockRequestDto
import com.mate.baedalmate.data.datasource.remote.block.BlockResponse
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.BlockRepository
import javax.inject.Inject

class BlockRepositoryImpl @Inject constructor(private val blockApiService: BlockApiService) :
    BlockRepository {
    override suspend fun requestPostBlockUser(data: BlockRequestDto): ApiResult<BlockResponse> =
        setExceptionHandling { blockApiService.requestPostBlockUser(data = data) }

    override suspend fun requestPostUnblockUser(data: BlockRequestDto): ApiResult<BlockResponse> =
        setExceptionHandling { blockApiService.requestPostUnblockUser(data = data) }
}