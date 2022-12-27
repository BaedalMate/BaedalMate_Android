package com.mate.baedalmate.domain.usecase.review

import com.mate.baedalmate.domain.repository.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestGetTargetReviewUserListUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(recruitId: Int) =
        reviewRepository.requestGetTargetReviewUserList(recruitId = recruitId)
}