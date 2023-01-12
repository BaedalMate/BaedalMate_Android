package com.mate.baedalmate.domain.usecase.block

import com.mate.baedalmate.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetBlockUserListUseCase @Inject constructor(private val blockRepository: BlockRepository) {
    suspend operator fun invoke() = blockRepository.requestGetBlockUserList()
}