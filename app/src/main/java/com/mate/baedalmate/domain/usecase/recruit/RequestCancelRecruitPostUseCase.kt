package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestCancelRecruitPostUseCase @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(id: Int) = recruitRepository.requestCancelRecruitPost(id = id)
}