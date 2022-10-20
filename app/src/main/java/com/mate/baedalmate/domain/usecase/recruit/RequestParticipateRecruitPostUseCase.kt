package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestParticipateRecruitPostUseCase @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(data: CreateOrderRequest) =
        recruitRepository.requestParticipateRecruitPost(data = data)
}