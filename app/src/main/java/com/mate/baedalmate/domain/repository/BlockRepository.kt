package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.block.BlockRequestDto
import com.mate.baedalmate.data.datasource.remote.block.BlockResponse
import com.mate.baedalmate.data.datasource.remote.block.BlockedUserListDto
import com.mate.baedalmate.domain.model.ApiResult

interface BlockRepository {
    suspend fun requestGetBlockUserList(): ApiResult<BlockedUserListDto>
    suspend fun requestPostBlockUser(data: BlockRequestDto): ApiResult<BlockResponse>
    suspend fun requestPostUnblockUser(data: BlockRequestDto): ApiResult<BlockResponse>
}