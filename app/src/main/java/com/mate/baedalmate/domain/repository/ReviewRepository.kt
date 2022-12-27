package com.mate.baedalmate.domain.repository

import com.mate.baedalmate.data.datasource.remote.review.CreateReviewDto
import com.mate.baedalmate.domain.model.ParticipantsDto
import retrofit2.Response

interface ReviewRepository {
    suspend fun requestGetTargetReviewUserList(recruitId: Int): Response<ParticipantsDto>
    suspend fun requestReviewUsers(body: CreateReviewDto): Response<Void>
}