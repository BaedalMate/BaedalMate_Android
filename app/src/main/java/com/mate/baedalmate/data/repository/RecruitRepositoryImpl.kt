package com.mate.baedalmate.data.repository

import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderRequest
import com.mate.baedalmate.data.datasource.remote.recruit.CreateOrderResponse
import com.mate.baedalmate.data.datasource.remote.recruit.DeleteOrderDto
import com.mate.baedalmate.data.datasource.remote.recruit.MainRecruitList
import com.mate.baedalmate.data.datasource.remote.recruit.RecruitApiService
import com.mate.baedalmate.data.datasource.remote.recruit.TagRecruitList
import com.mate.baedalmate.domain.model.ApiResult
import com.mate.baedalmate.domain.model.RecruitDetail
import com.mate.baedalmate.domain.model.RecruitList
import com.mate.baedalmate.domain.model.setExceptionHandling
import com.mate.baedalmate.domain.repository.RecruitRepository
import javax.inject.Inject

class RecruitRepositoryImpl @Inject constructor(private val recruitApiService: RecruitApiService) :
    RecruitRepository {
    override suspend fun requestRecruitList(
        categoryId: Int?,
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<RecruitList> =
        setExceptionHandling {
            recruitApiService.requestRecruitList(
                categoryId = categoryId, page = page, size = size, sort = sort
            )
        }

    override suspend fun requestRecruitMainList(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<MainRecruitList> =
        setExceptionHandling {
            recruitApiService.requestRecruitMainList(page = page, size = size, sort = sort)
        }

    override suspend fun requestRecruitTagList(
        page: Int,
        size: Int,
        sort: String
    ): ApiResult<TagRecruitList> =
        setExceptionHandling {
            recruitApiService.requestRecruitTagList(
                page = page,
                size = size,
                sort = sort
            )
        }

    override suspend fun requestRecruitPost(id: Int): ApiResult<RecruitDetail> =
        setExceptionHandling {
            recruitApiService.requestRecruitPost(id = id)
        }

    override suspend fun requestCancelRecruitPost(id: Int): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCancelRecruitPost(id = id)
        }

    override suspend fun requestCloseRecruitPost(id: Int): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCloseRecruitPost(id = id)
        }

    override suspend fun requestParticipateRecruitPost(data: CreateOrderRequest): ApiResult<CreateOrderResponse> =
        setExceptionHandling {
            recruitApiService.requestParticipateRecruitPost(data = data)
        }

    override suspend fun requestCancelParticipateRecruitPost(data: DeleteOrderDto): ApiResult<Void> =
        setExceptionHandling {
            recruitApiService.requestCancelParticipateRecruitPost(data = data)
        }
}