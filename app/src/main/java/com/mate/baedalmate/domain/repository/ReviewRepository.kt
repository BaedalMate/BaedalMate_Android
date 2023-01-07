package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.review.CreateReviewDto
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.ParticipantsDto

interface ReviewRepository {
    suspend fun requestGetTargetReviewUserList(recruitId: Int): ApiResult<ParticipantsDto>
    suspend fun requestReviewUsers(body: CreateReviewDto): ApiResult<Void>
}