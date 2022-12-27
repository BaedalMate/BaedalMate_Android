package com.mate.baedalmate.domain.usecase.review

import com.mate.baedalmate.data.datasource.remote.review.CreateReviewDto
import com.mate.baedalmate.domain.repository.ReviewRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestReviewUsersUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(body: CreateReviewDto) =
        reviewRepository.requestReviewUsers(body = body)
}