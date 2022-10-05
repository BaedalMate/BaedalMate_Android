package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRecruitListUseCase  @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(categoryId: Int, page: Int, size: Int, sort: String) =
        recruitRepository.requestRecruitList(categoryId = categoryId, page = page, size = size, sort = sort)
}