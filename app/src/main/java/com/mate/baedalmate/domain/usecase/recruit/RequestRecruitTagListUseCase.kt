package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRecruitTagListUseCase  @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(page: Int, size: Int, sort: String) =
        recruitRepository.requestRecruitTagList(page = page, size = size, sort = sort)
}