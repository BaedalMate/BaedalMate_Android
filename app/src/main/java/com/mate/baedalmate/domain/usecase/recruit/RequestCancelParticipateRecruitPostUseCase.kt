package com.mate.baedalmate.domain.usecase.recruit

import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestCancelParticipateRecruitPostUseCase @Inject constructor(private val recruitRepository: RecruitRepository) {
    suspend operator fun invoke(data: DeleteOrderDto) =
        recruitRepository.requestCancelParticipateRecruitPost(data = data)
}