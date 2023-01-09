package com.mate.baedalmate.domain.usecase.block

import com.mate.baedalmate.data.datasource.remote.block.BlockRequestDto
import com.mate.baedalmate.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestPostUnblockUserUseCase @Inject constructor(private val blockRepository: BlockRepository) {
    suspend operator fun invoke(blockRequestInfo: BlockRequestDto) =
        blockRepository.requestPostUnblockUser(
            data = blockRequestInfo
        )
}