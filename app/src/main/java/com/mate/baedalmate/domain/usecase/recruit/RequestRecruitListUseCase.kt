package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.domain.repository.RecruitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRecruitListUseCase  @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(categoryId: Int?= null, exceptClose: Boolean, sort: String) =
        recruitRepository.requestRecruitList(categoryId = categoryId,exceptClose = exceptClose, sort = sort)
            .flowOn(Dispatchers.Default)
}