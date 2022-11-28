package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitList
import com.mate.baedalmate.domain.repository.RecruitRepository
import retrofit2.Response
import javax.inject.Inject

class RecruitRepositoryImpl @Inject constructor(private val recruitApiService: RecruitApiService) :
    RecruitRepository {
    override suspend fun requestRecruitList(
        categoryId: Int?,
        page: Int,
        size: Int,
        sort: String
    ): Response<RecruitList> = recruitApiService.requestRecruitList(
        categoryId = categoryId, page = page, size = size, sort = sort
    )

    override suspend fun requestRecruitMainList(
        page: Int,
        size: Int,
        sort: String
    ): Response<MainRecruitList> =
        recruitApiService.requestRecruitMainList(page = page, size = size, sort = sort)

    override suspend fun requestRecruitTagList(
        page: Int,
        size: Int,
        sort: String
    ): Response<TagRecruitList> =
        recruitApiService.requestRecruitTagList(page = page, size = size, sort = sort)

    override suspend fun requestRecruitPost(id: Int): Response<RecruitDetail> =
        recruitApiService.requestRecruitPost(id = id)

    override suspend fun requestCancelRecruitPost(id: Int): Response<Void> =
        recruitApiService.requestCancelRecruitPost(id = id)

    override suspend fun requestCloseRecruitPost(id: Int): Response<Void> =
        recruitApiService.requestCloseRecruitPost(id = id)

    override suspend fun requestParticipateRecruitPost(data: CreateOrderRequest): Response<CreateOrderResponse> =
        recruitApiService.requestParticipateRecruitPost(data = data)

    override suspend fun requestCancelParticipateRecruitPost(data: DeleteOrderDto): Response<Void> =
        recruitApiService.requestCancelParticipateRecruitPost(data = data)
}