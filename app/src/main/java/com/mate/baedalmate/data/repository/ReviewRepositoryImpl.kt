package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.review.CreateReviewDto
import com.mate.baedalmate.data.datasource.remote.review.ReviewApiService
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.ParticipantsDto
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.ReviewRepository
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewApiService: ReviewApiService) :
    ReviewRepository {
    override suspend fun requestGetTargetReviewUserList(recruitId: Int): ApiResult<ParticipantsDto> =
        setExceptionHandling { reviewApiService.requestGetTargetReviewUserList(id = recruitId) }

    override suspend fun requestReviewUsers(body: CreateReviewDto): ApiResult<Void> =
        setExceptionHandling { reviewApiService.requestReviewUsers(data = body) }
}